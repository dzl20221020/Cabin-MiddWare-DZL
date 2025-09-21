package com.hdu.neurostudent_signalflow.experiment;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ExperimentStateMachine {

    private static final ExperimentStateMachine INSTANCE = new ExperimentStateMachine();
    private final AtomicReference<ExperimentState> currentState = new AtomicReference<>(ExperimentState.NOT_STARTED);

    // 状态转换表
    private final Map<ExperimentState, Map<ExperimentEvent, ExperimentState>> transitionTable = new HashMap<>();

    private ExperimentStateMachine() {
        initTransitions();
    }

    public static ExperimentStateMachine getInstance() {
        return INSTANCE;
    }

    private void initTransitions() {
        // NOT_STARTED 状态
        transitionTable.put(ExperimentState.NOT_STARTED, Map.of(
                ExperimentEvent.START_PREPARATION, ExperimentState.PREPARING,
                ExperimentEvent.ERROR_OCCURED, ExperimentState.ERROR
        ));

        // PREPARING 状态
        transitionTable.put(ExperimentState.PREPARING, Map.of(
                ExperimentEvent.START_EXPERIMENT, ExperimentState.RUNNING,
                ExperimentEvent.ERROR_OCCURED, ExperimentState.ERROR
        ));

        // RUNNING 状态
        transitionTable.put(ExperimentState.RUNNING, Map.of(
                ExperimentEvent.PAUSE_EXPERIMENT, ExperimentState.PAUSED,
                ExperimentEvent.END_EXPERIMENT, ExperimentState.ENDED,
                ExperimentEvent.ERROR_OCCURED, ExperimentState.ERROR
        ));

        // PAUSED 状态
        transitionTable.put(ExperimentState.PAUSED, Map.of(
                ExperimentEvent.RESUME_EXPERIMENT, ExperimentState.RUNNING,
                ExperimentEvent.END_EXPERIMENT, ExperimentState.ENDED,
                ExperimentEvent.ERROR_OCCURED, ExperimentState.ERROR
        ));

        // ENDED 状态（终态）
        transitionTable.put(ExperimentState.ENDED, Map.of(
                ExperimentEvent.ERROR_OCCURED, ExperimentState.ERROR
        ));

        // ERROR 状态（终态）
        transitionTable.put(ExperimentState.ERROR, Collections.emptyMap());
    }

    // 处理事件，返回是否状态改变成功
    public boolean handleEvent(ExperimentEvent event) {
        ExperimentState prevState;
        ExperimentState nextState;

        do {
            prevState = currentState.get();
            Map<ExperimentEvent, ExperimentState> possibleTransitions = transitionTable.getOrDefault(prevState, Map.of());
            nextState = possibleTransitions.get(event);

            if (nextState == null) {
                System.out.println("Invalid event " + event + " for state " + prevState);
                return false; // 无效事件
            }
        } while (!currentState.compareAndSet(prevState, nextState));

        System.out.println("State changed: " + prevState + " → " + nextState);
        return true;
    }

    public ExperimentState getCurrentState() {
        return currentState.get();
    }

    public boolean isTerminalState() {
        return currentState.get() == ExperimentState.ENDED || currentState.get() == ExperimentState.ERROR;
    }
}
