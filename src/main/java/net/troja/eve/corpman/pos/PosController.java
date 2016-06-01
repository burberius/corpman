package net.troja.eve.corpman.pos;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import net.troja.eve.corpman.Configuration;
import net.troja.eve.corpman.ConfigurationChangeListener;
import net.troja.eve.corpman.ConfigurationManager;
import net.troja.eve.corpman.evedata.InvTypesRepository;
import net.troja.eve.corpman.pos.db.Pos;
import net.troja.eve.corpman.pos.db.PosModule;
import net.troja.eve.corpman.pos.web.PosModuleWebBean;
import net.troja.eve.corpman.pos.web.PosWebBean;
import net.troja.eve.corpman.xmpp.XmppClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pos")
public class PosController implements ConfigurationChangeListener {
    private static final Logger LOGGER = LogManager.getLogger(PosController.class);
    private static final long CACHE_TIME = 1000 * 60 * 15;
    private static final Set<Integer> broadcastTimes = new HashSet<>(Arrays.asList(new Integer[] { 1, 2, 3, 6, 12, 24, 36 }));

    @Autowired
    private PosManagingController posController;
    @Autowired
    private PosModuleController posModuleController;
    @Autowired
    private InvTypesRepository invTypeRepository;
    @Autowired
    private PosMerger posMerger;
    @Autowired
    private PosHelper posHelper;
    @Autowired
    private PosCalculator posCalculator;
    @Autowired
    private PosStructurizer posStructurizer;
    @Autowired
    private XmppClient xmppClient;
    @Autowired
    private ConfigurationManager configManager;

    private List<PosWebBean> cachedPosList;
    private long posListCachedUntil;
    private int hoursToShow = 48;
    private int hoursToBroadcast = 24;
    private int lastBroadcastMessageCount = 0;
    private final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

    @RequestMapping("/all")
    public List<PosWebBean> getPosList() {
        if (posListCachedUntil < System.currentTimeMillis()) {
            cachedPosList = new ArrayList<>();
            final List<PosModule> modules = posModuleController.getModules();
            for (final Pos pos : posController.getPosList()) {
                final PosWebBean posWebBean = posHelper.getPosWebBean(pos);
                final List<PosModule> modulesForPos = posHelper.getModulesForPos(modules, pos);
                for (final PosModule module : modulesForPos) {
                    final PosModuleWebBean destination = new PosModuleWebBean(module);
                    if (PosHelper.isContainer(module.getTypeID())) {
                        calculateContent(destination, module, modulesForPos);
                    }
                    posWebBean.addModule(destination);
                }
                cachedPosList.add(posWebBean);
            }
            posListCachedUntil = System.currentTimeMillis() + CACHE_TIME;
        }
        return cachedPosList;
    }

    @RequestMapping("/modules")
    public List<PosModuleWebBean> getPosModuleList() {
        final List<PosModuleWebBean> moduleList = new ArrayList<PosModuleWebBean>();
        for (final PosModule module : posModuleController.getModules()) {
            if (module.getPos() == null) {
                final PosModuleWebBean webModule = new PosModuleWebBean(module);
                if (PosHelper.isContainer(module.getTypeID())) {
                    calculateContent(webModule, module, null);
                }
                final List<Pos> poses = posHelper.findMatchingPoses(posController.getPosList(), module.getSystem());
                for (final Pos match : poses) {
                    webModule.addPossiblePos(match.getItemID(), match.getMoon() + " - " + match.getName());
                }
                moduleList.add(webModule);
            }
        }
        return moduleList;
    }

    @RequestMapping(value = "/modules/{itemId}", method = RequestMethod.PUT)
    public void updatePosModule(@PathVariable final Long itemId, @RequestBody final PosModuleWebBean module) {
        if (module == null) {
            return;
        }
        LOGGER.info("Updating " + module.getType() + " in " + module.getSystem());
        final PosModule mod = posModuleController.getModule(itemId);
        if ((mod != null) && (module.getPosItemID() != null)) {
            final Pos pos = posController.getPos(module.getPosItemID());
            if (pos != null) {
                mod.setPos(pos);
                posModuleController.updateModule(mod);
                final List<PosModule> modules = posModuleController.getModules();
                final List<PosModule> modulesForPos = posHelper.getModulesForPos(modules, pos);
                posMerger.matchModules(modulesForPos);
                posStructurizer.calculateStructure(modulesForPos);
                posModuleController.updateModules(modules);
                posListCachedUntil = 0;
            }
        }
    }

    @RequestMapping("/status")
    public Map<String, Date> getStatus() {
        final Map<String, Date> status = new HashMap<>();
        status.put("posLastUpdate", posController.getLastUpdate());
        status.put("posCachedUntil", posController.getCachedUntil());
        status.put("moduleLastUpdate", posModuleController.getCurrentTime());
        status.put("moduleCachedUntil", posModuleController.getCachedUntil());
        return status;
    }

    private void calculateContent(final PosModuleWebBean posModule, final PosModule module, final List<PosModule> modList) {
        int timeLeft = 0;
        final int perRun = module.getChangeQuantity();
        final int containerVolume = posHelper.getMaxContainerVolume(module);
        final Double volume = invTypeRepository.getVolume(posModule.getContentTypeID());
        final int containerQuantity = (int) (containerVolume / volume);
        int quantity = posCalculator.getFixedQuantity(module, perRun);
        if (containerQuantity < quantity) {
            quantity = containerQuantity;
        }
        if (quantity < 0) {
            quantity = 0;
        }
        posModule.setQuantity(quantity);
        posModule.setVolume(volume * quantity);
        posModule.setContainerQuantity(containerQuantity);
        posModule.setPercent((posModule.getQuantity() / (double) containerQuantity) * 100d);
        if (perRun > 0) {
            timeLeft = (int) Math.floor((containerQuantity - (double) quantity) / perRun);
            posModule.setInput(true);
        } else if (perRun < 0) {
            timeLeft = (int) Math.floor(module.getQuantity() / Math.abs(perRun));
            posModule.setInput(false);
        }
        posModule.setTimeLeft(timeLeft);
    }

    @PostConstruct
    public void initChecks() {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (!xmppClient.isEnabled()) {
            return;
        }
        configManager.addChangeListener(this);
        configurationChanged(configManager.getConfiguration());

        xmppClient.addCommand("pos", "Get the current POS states", (chat, who) -> {
            final List<String> posStates = getPosStates().messages;
            posStates.addAll(getFuelAndModuleStates(hoursToShow).messages);
            try {
                if (posStates.size() == 0) {
                    chat.sendMessage("Everything fine for the next " + hoursToShow + " hours!");
                } else {
                    chat.sendMessage("Due in the next " + hoursToShow + " hour are:");
                    for (final String mess : posStates) {
                        chat.sendMessage(mess);
                    }
                }
            } catch (final NotConnectedException e) {
                LOGGER.error("Couldn't send message " + e.getMessage(), e);
            }
        });

        posModuleController.setBroadcastTrigger(() -> {
            checkFuelAndModuleState();
        });
        posController.setBroadcastTrigger(() -> {
            checkPosState();
        });
    }

    public void checkPosState() {
        LOGGER.info("Check POS states for broadcast");
        final PosState posStates = getPosStates();
        if (posStates.messages.size() > 0) {
            final int min = (int) Math.floor(posStates.minTime / 6d);
            LOGGER.info("Min: " + min + " " + posStates.minTime + " " + ((posStates.minTime % 6) == 0));
            if ((posStates.messages.size() > lastBroadcastMessageCount) || (broadcastTimes.contains(min) && ((posStates.minTime % 6) == 0))) {
                xmppClient.broadcast("Broadcast for POS states:");
                for (final String state : posStates.messages) {
                    xmppClient.broadcast(state);
                }
            }
        }
        lastBroadcastMessageCount = posStates.messages.size();
    }

    public void checkFuelAndModuleState() {
        LOGGER.info("Check fuel and module states for broadcast");
        final PosState state = getFuelAndModuleStates(hoursToBroadcast);
        LOGGER.info(" mintime: " + state.minTime + " " + broadcastTimes.contains(state.minTime));
        if ((state.messages.size() > 0) && broadcastTimes.contains(state.minTime)) {
            xmppClient.broadcast("Broadcast for fuel and module states:");
            for (final String message : state.messages) {
                xmppClient.broadcast(message);
            }
        }
    }

    private PosState getPosStates() {
        final List<String> problemsHigh = new ArrayList<String>();
        Long minTime = Long.MAX_VALUE;
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        final long now = calendar.getTimeInMillis();
        for (final PosWebBean pos : getPosList()) {
            if (!pos.getState().equals("Online")) {
                final Date stateDate = new Date(pos.getStateTimestamp());
                problemsHigh.add(pos.getName() + " " + pos.getSystem() + " " + pos.getMoon() + " State: " + pos.getState() + " until "
                        + dateFormat.format(stateDate) + " ET");
                final long diff = stateDate.getTime() - now;
                if (diff < minTime) {
                    minTime = diff;
                }
            }
        }
        return new PosState(problemsHigh, (int) Math.floor(minTime / 600000d));
    }

    private PosState getFuelAndModuleStates(final int hours) {
        LOGGER.info("getFuelAndModuleStates");
        final List<String> problems = new ArrayList<String>();
        int minTime = Integer.MAX_VALUE;
        for (final PosWebBean pos : getPosList()) {
            if (pos.getFuelRunTime() <= hours) {
                problems.add(pos.getName() + " " + pos.getSystem() + " " + pos.getMoon() + " ==> Fuel for : "
                        + PosHelper.createNiceTimeString(pos.getFuelRunTime()));
                if (pos.getFuelRunTime() < minTime) {
                    minTime = pos.getFuelRunTime();
                }
            }
            final Map<Long, List<PosModuleWebBean>> contentList = new HashMap<Long, List<PosModuleWebBean>>();
            for (final PosModuleWebBean module : pos.getModules()) {
                if (!PosHelper.isContainer(module.getTypeID())) {
                    continue;
                }
                List<PosModuleWebBean> list = contentList.get(module.getContentTypeID());
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(module);
                contentList.put(module.getContentTypeID(), list);
            }
            for (final Entry<Long, List<PosModuleWebBean>> entry : contentList.entrySet()) {
                final List<PosModuleWebBean> modules = entry.getValue();
                final PosModuleWebBean module = modules.get(0);
                int timeLeft = 0;
                int quantity = 0;
                int containerQuantity = 0;
                for (int count = 0; count < modules.size(); count++) {
                    final PosModuleWebBean mod = modules.get(count);
                    timeLeft += mod.getTimeLeft();
                    quantity += mod.getQuantity();
                    containerQuantity += mod.getContainerQuantity();
                }
                if (timeLeft <= hours) {
                    double percent = 0;
                    String toDo = "Fill";
                    if (module.isInput()) {
                        toDo = "Empty";
                        percent = ((containerQuantity - quantity) / (double) containerQuantity) * 100d;
                    } else {
                        percent = (quantity / (double) containerQuantity) * 100d;
                    }
                    final String percentString = String.format("%.1f%%", percent);
                    problems.add(pos.getName() + " " + pos.getSystem() + " " + pos.getMoon() + " ==> " + toDo + " " + module.getContent()
                            + " -> Left : " + PosHelper.createNiceTimeString(timeLeft) + " / " + percentString);
                    if (timeLeft < minTime) {
                        minTime = timeLeft;
                    }
                }
            }
        }
        LOGGER.info("min " + minTime);
        return new PosState(problems, minTime);
    }

    private class PosState {
        List<String> messages;
        int minTime;

        public PosState(final List<String> messages, final int minTime) {
            this.messages = messages;
            this.minTime = minTime;
        }
    }

    @Override
    public void configurationChanged(final Configuration configuration) {
        if (configuration != null) {
            hoursToShow = configuration.getPosAlertHours();
            hoursToBroadcast = configuration.getPosBroadcastHours();
        }
    }
}
