package it.smartcommunitylabdhub.fsm;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StateMachineTest {

    // some method definition

    @Test
    public void fsm() {
        // Create the state machine

        FsmState<String, String, Map<String, Object>> state1 = new FsmState<>();
        FsmState<String, String, Map<String, Object>> state2 = new FsmState<>();
        FsmState<String, String, Map<String, Object>> state3 = new FsmState<>();
        FsmState<String, String, Map<String, Object>> state4 = new FsmState<>();
        FsmState<String, String, Map<String, Object>> errorState = new FsmState<>(); // Error
        // state

        // Create the initial state and context
        String initialState = "State1";
        Map<String, Object> initialContext = new HashMap<>();

        // Create the state machine using the builder
        Fsm.Builder<String, String, Map<String, Object>> builder = Fsm
                .<String, String, Map<String, Object>>builder(initialState, initialContext)
                .withState("State1", state1)
                .withFsm()
                .withState("State2", state2)
                .withFsm()
                .withState("State3", state3)
                .withFsm()
                .withState("State4", state4)
                .withFsm()
                .withErrorState("ErrorState", errorState)
                .withStateChangeListener((newState, context) ->
                        System.out.println("State Change Listener: " + newState + ", context: " + context)
                );

        // Define transactions for state 1
        state1.setTransaction(new Transaction<>("Event1", "State2", (context, input) -> true));

        // Define transactions for state 2
        state2.setTransaction(new Transaction<>("Event2", "State3", (context, input) -> true));

        // Define transactions for state 3
        state3.setTransaction(new Transaction<>("Event3", "State4", (context, input) -> true));

        // Define transactions for state 4
        state4.setTransaction(new Transaction<>("Event4", "State1", (context, input) -> true));

        // Set internal logic for state 1
        state1.setInternalLogic((context, input, stateMachine) -> {
            System.out.println("Executing internal logic of State1 with context: " + context);
            Optional.ofNullable(context).ifPresent(c -> c.put("value", 1));

            return Optional.of("State1 Result");
        });
        state1.setExitAction(context -> {
            System.out.println("exit action for state 1");
        });
        // Set internal logic for state 2
        state2.setInternalLogic((context, input, stateMachine) -> {
            System.out.println("Executing internal logic of State2 with  context: " + context);
            Optional.ofNullable(context).ifPresent(c -> c.put("value", 2));
            return Optional.of("State2 Result");
        });

        // Set internal logic for state 3
        state3.setInternalLogic((context, input, stateMachine) -> {
            System.out.println("Executing internal logic of State3 with context: " + context);
            Optional.ofNullable(context).ifPresent(c -> c.put("value", 3));
            return Optional.of("State3 Result");
        });

        // Set internal logic for state 4
        state4.setInternalLogic((context, input, stateMachine) -> {
            System.out.println("Executing internal logic of State4 with  context: " + context);
            Optional.ofNullable(context).ifPresent(c -> c.put("value", 4));
            return Optional.of("State4 Result");
        });

        // Set internal logic for the error state
        errorState.setInternalLogic((context, input, stateMachine) -> {
            System.out.println("Error state reached. context: " + context);
            // Handle error logic here
            return Optional.empty(); // No result for error state
        });

        // Add event listeners
        builder.withEventListener(
                "Event1",
                (context, input) -> System.out.println("Event1 Listener: context: " + context)
        );

        builder.withEventListener(
                "Event2",
                (context, input) -> System.out.println("Event2 Listener: context: " + context)
        );
        builder.withEventListener(
                "Event3",
                (context, input) -> System.out.println("Event3 Listener: context: " + context)
        );
        builder.withEventListener(
                "Event4",
                (context, input) -> System.out.println("Event4 Listener: context: " + context)
        );
        // Build the state machine
        Fsm<String, String, Map<String, Object>> stateMachine = builder.build();

        // Trigger events to test the state machine
        stateMachine.goToState("State2", null);

        stateMachine.goToState("State3", null);
        stateMachine.goToState("State4", null);
        // try {
        // String ser = stateMachine.serialize();

        // System.out.println(ser);

        // StateMachine<String, String, Map<String, Object>> stateMachine2 =
        // StateMachine.deserialize(ser);
        // System.out.println("Deserialize");
        // } catch (Exception e) {
        // System.out.println(e.getMessage());
        // }

    }
}
