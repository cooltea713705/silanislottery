package com.rros.silanislottery;

import org.apache.commons.cli.*;

import java.io.Console;
import java.util.Arrays;

/**
 * Entry point for running the application
 */
public class Main {

    public static final String POT_CLI_OPTION = "pot";
    public static final String HELP_CLI_OPTION = "help";
    /**
     * System console
     * <p>
     * Use of java.io.Console eases overall handling but has a drawback: testing requires the use of a terminal to run
     * the application (running in the IDE results in System.console() being null).
     */
    private final static Console SYSTEM_CONSOLE = System.console();
    /**
     * Command line options handling
     */
    private final static Options CLI_OPTIONS;
    /**
     * Silanis Lottery
     */
    private static SilanisLottery LOTTERY;
    private static CommandLineParser parser = new DefaultParser();
    private static HelpFormatter helpFormatter = new HelpFormatter();

    static {
        CLI_OPTIONS = new Options();
        Main.CLI_OPTIONS.addOption(Main.HELP_CLI_OPTION, false, "display this help");
        Main.CLI_OPTIONS.addOption(Main.POT_CLI_OPTION, true, "initial pot value (default: " + SilanisLottery.INITIAL_POT + ")");
    }

    private Main() {
        // private so this class cannot be instantiated
    }

    public static void main(final String[] args) {
        if (SYSTEM_CONSOLE == null) {
            System.err.println("No console available.");
            System.exit(1);
        }

        final CommandLine line;
        try {
            line = parser.parse(CLI_OPTIONS, args, false);
            if (line.hasOption(HELP_CLI_OPTION)) {
                commandLineHelp();
                System.exit(0);
            } else if (line.hasOption(POT_CLI_OPTION)) {
                try {
                    int initialPot = Integer.valueOf(line.getOptionValue(POT_CLI_OPTION));
                    LOTTERY = new SilanisLottery(initialPot);
                } catch (NumberFormatException e) {
                    System.err.println("Unexpected " + POT_CLI_OPTION + " option value: expects an integer");
                    System.exit(1);
                }
            } else {
                LOTTERY = new SilanisLottery();
            }


            splash();

            //noinspection InfiniteLoopStatement
            while (true) {
                final String inputCommand = SYSTEM_CONSOLE.readLine("Current pot: %d$ > ", LOTTERY.getPot());
                interpretCommand(inputCommand);
            }
        } catch (ParseException exp) {
            System.err.println(exp.getMessage());
            System.exit(1);
        }
    }

    private static void commandLineHelp() {
        helpFormatter.printHelp("java " + Main.class.getCanonicalName(), "Opens a command line application to handle the Silanis lottery", CLI_OPTIONS, "", true);
    }

    /**
     * Handles input command
     *
     * @param inputCommand input command
     */
    private static void interpretCommand(final String inputCommand) {
        if (inputCommand == null) {
            // handles Ctrl-D input
            exit();
        }

        //noinspection ConstantConditions
        switch (inputCommand) {
            // list commands here
            // nice-to-have commands as enum
            case "help":
                getHelp();
                break;
            case "draw":
                draw();
                break;
            case "winners":
                winners();
                break;
            case "exit":
                exit();
                break;
            default:
                if (inputCommand.matches("purchase.*")) {
                    // buyerName obtained from removing the prefix to input command
                    final String buyerName = inputCommand.replaceFirst("purchase\\s*", "");
                    purchase(buyerName);
                } else if (!inputCommand.isEmpty()) {
                    SYSTEM_CONSOLE.format("Unknown command: \"%s\"%n", inputCommand);
                }
                // else inputCommand is empty: display a new prompt
                break;
        }

    }

    /**
     * Handle purchase command
     *
     * @param buyerName input buyer name
     */
    private static void purchase(final String buyerName) {
        try {
            SYSTEM_CONSOLE.format("Ticket %d was purchased by %s.%n", LOTTERY.purchaseTicket(buyerName), buyerName);
        } catch (NoAvailableTicketException | InvalidBuyerNameException e) {
            SYSTEM_CONSOLE.format("%s%n", e.getMessage());
        }
    }

    /**
     * Handle exit command
     */
    private static void exit() {
        // TODO if lottery is ongoing: are you sure?
        SYSTEM_CONSOLE.format("Closing the application, the current pot is: %d$%n", LOTTERY.getPot());
        System.exit(0);
    }

    /**
     * Handle winners command
     */
    private static void winners() {
        try {
            SYSTEM_CONSOLE.format("%s%n", LOTTERY.generateWinnersMessage());
        } catch (NoPreviousDrawException e) {
            SYSTEM_CONSOLE.format("%s%n", e.getMessage());
        }
    }

    /**
     * Handle draw command
     */
    private static void draw() {
        SYSTEM_CONSOLE.format("Lottery draw: %s%n", Arrays.toString(LOTTERY.drawLottery()));
        SYSTEM_CONSOLE.format("The prizes of the winners (if there are any) are now subtracted from the pot.%n", Arrays.toString(LOTTERY.drawLottery()));
    }

    /**
     * Display splash
     */
    private static void splash() {
        SYSTEM_CONSOLE.format("Welcome to the Silanis Lottery!%nThe current pot is: %d$%nThe following commands are available:%n", LOTTERY.getPot());
        getHelp();
    }

    /**
     * Handle help command
     */
    private static void getHelp() {
        SYSTEM_CONSOLE.format("help\tGet this help message%n");
        SYSTEM_CONSOLE.format("purchase %%buyer's first name%%\tPurchase a ticket (%d$)%n", SilanisLottery.TICKET_PRICE);
        SYSTEM_CONSOLE.format("draw\tDraw lottery%n");
        SYSTEM_CONSOLE.format("winners\tDisplay winners%n");
        SYSTEM_CONSOLE.format("exit\tExit this application%n");
    }
}
