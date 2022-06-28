package game.logics.records;

import game.logics.entities.player.Player;
import game.logics.entities.player.Player.PlayerDeath;

import game.logics.handler.Logics.GameInfo;
import game.utility.input.JSONReader;
import game.utility.input.JSONReaderImpl;
import game.utility.input.JSONWriter;
import game.utility.input.JSONWriterImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

/**
 * This class handles statistics and records, furthermore it supersedes the
 * reading and writing of them.
 */
public final class Records {

    private static final int NUMBER_OF_SAVED_RECORD = 3;

    private final Supplier<GameInfo> getGame;
    private final Player player;
    private final JSONWriter writer;
    private final JSONReader reader;

    // Statistics and records list
    private int burnedTimes;
    private int zappedTimes;

    // Data read from game
    private static int playingRecordScore; // higher score obtained by playing consecutively
    private static boolean newPlayingRecordScore;

    private final List<Integer> recordScores = new ArrayList<>(Records.NUMBER_OF_SAVED_RECORD); // absolute new score record
    private boolean newRecordScore;

    /*{
        recordScores.addAll(Collections.nCopies(Records.NUMBER_OF_SAVED_RECORD, Optional.empty()));
    }*/

    /**
     * Build a new {@link Records}.
     * @param getGame {@link Supplier} of {@link GameInfo} objects used to get
     *   current game informations.
     * @param player {@link Player} object used to get some player informations.
     */
    public Records(final Supplier<GameInfo> getGame, final Player player) {
        this.getGame = getGame;

        this.writer = new JSONWriterImpl(this);
        this.reader = new JSONReaderImpl(this);

        //this.game.getNumbersOfGamesPlayed();
        this.player = player;
    }

    /****************************************/
    /***    In and to file operations     ***/
    /****************************************/

    /**
     * Get data for updating in game, calling the data getters.
     *
     * @param newGameInfo {@link GameInfo} instance containing
     * final game data
     */
    public void fetch(final GameInfo newGameInfo) {

        final PlayerDeath causeOfDeath;

        //System.out.println(newGameInfo.isGamePlayed());
        //newGameInfo.getGameDate().ifPresent(System.out::println);

        if (player.hasDied()) {
            causeOfDeath = player.getCauseOfDeath();
            switch (causeOfDeath) {
                case BURNED:
                    this.burnedTimes++;
                    break;
                case ZAPPED:
                    this.zappedTimes++;
                    break;
                default:
                    break;
            }

            this.checkScore(newGameInfo.getFinalScore());
        }
    }

    /**
     * Read from file.
     */
    public void refresh() {
        this.reader.read();
    }

    /**
     * Write to file.
     */
    public void update() {
        this.writer.write();
    }

    /****************************************/
    /***   Calculate and check records    ***/
    /****************************************/

    /**
     * This method checks if the new finalScore is a new record and only in
     * this case saves it.
     *
     * @param finalScore
     *   final score in the current game
     */
    public void checkScore(final int finalScore) {

       // final BiPredicate<GameUID, GameUID> checkIfNew =
       //         (oldUID,newUID) -> oldUID.getGameDate() != newUID.getGameDate();
       // checkIfNew.test(UIDGame, newGameUID)

        if (finalScore > Records.playingRecordScore) {
            Records.newPlayingRecordScore = true;
            Records.playingRecordScore = finalScore;
        } else if (finalScore < Records.playingRecordScore) {
            Records.newPlayingRecordScore = false;
        }

        if (finalScore > this.getHighestScore()) {
            this.newRecordScore = true;
            this.addRecordScore(finalScore);
        } else {
            this.newRecordScore = false;
        }

        if (finalScore < this.getHighestScore()
                && finalScore > this.getLowestRecordScore()
                || this.recordScores.size() < Records.getSavedNumberOfRecords()) {
            this.addRecordScore(finalScore);
        }
    }

    /****************************************/
    /*** Getters & Setters from / to file ***/
    /****************************************/

    /**
     * This static method is used to get the constant value stored as
     * {@link Records#NUMBER_OF_SAVED_RECORD}.
     *
     * @return the number of records that will be written to file.
     */
    public static int getSavedNumberOfRecords() {
        return Records.NUMBER_OF_SAVED_RECORD;
    }

    /**
     * This method is used to get burnedTimes value.
     *
     * @return how many times Barry died burned.
     */
    public int getBurnedTimes() {
        return this.burnedTimes;
    }

    /**
     * This method is used to get zappedTimes value.
     *
     * @return how many times Barry died electrocuted.
     */
    public int getZappedTimes() {
        return this.zappedTimes;
    }

    /**
     * This method is used to set burnedTimes value.
     *
     * @param readBurnedTimes how many times Barry died burned.
     */
    public void setBurnedTimes(final int readBurnedTimes) {
        this.burnedTimes = readBurnedTimes;
    }

    /**
     * This method is used to set zappedTimes value.
     *
     * @param readZappedTimes how many times Barry died electrocuted.
     */
    public void setZappedTimes(final int readZappedTimes) {
        this.zappedTimes = readZappedTimes;
    }

    // TODO: javadoc
    /**
     * 
     * @param newRecordScore
     */
    public void addRecordScore(final int newRecordScore) {
        //this.recordScores.forEach(System.out::println);

        this.recordScores.add(newRecordScore);
        this.recordScores.sort(Comparator.reverseOrder());

        if (this.recordScores.size() > Records.getSavedNumberOfRecords()) {
            //System.out.println("ELIMINATO: "
            //    + this.recordScores.get(Records.getSavedNumberOfRecords()).toString());
            this.recordScores.remove(Records.getSavedNumberOfRecords());
        }
    }

    // TODO: javadoc
    /**
     * 
     * @return .
     */
    public List<Integer> getRecordScores() {
        /*final List<Integer> recordScores = new ArrayList<>();

        for (final Optional<Integer> record : this.recordScores) {
            if (record.isPresent()) {
                recordScores.add(record.get());
            }
        }*/
        return List.copyOf(this.recordScores);
    }

    // TODO: javadoc 
    /**
     * 
     * @param recordScores
     */
    public void setRecordScores(final List<Integer> recordScores) {
        this.recordScores.clear();
        this.recordScores.addAll(recordScores);
        /*for (final Integer record : recordScores) {
                this.recordScores.add(Optional.of(record));
        }
        if (recordScores.size() < Records.getSavedNumberOfRecords()) {
            this.recordScores.addAll(Collections.nCopies(
                    recordScores.size() - Records.NUMBER_OF_SAVED_RECORD,
                    Optional.empty()));
        }*/
    }

    /****************************************/
    /*** Getters & Setters from / to game ***/
    /****************************************/

    /**
     * Get player score form {@link game.logics.handler.Logics.GameInfo GameInfo} instance.
     * @return the player score
     */
    public int getScore() {
        return this.getGame.get().getFinalScore();
    }

    /**
     * Get current highest score obtained by player.
     * @return the first element of the highest scores list
     */
    public int getHighestScore() {
        if (this.recordScores.isEmpty()) {
            return 0;
        } else {
            return this.recordScores.get(0);
        }
    }

    /**
     * Get current least score obtained by player.
     * @return the last element of the highest scores list
     */
    private int getLowestRecordScore() {
        if (this.recordScores.isEmpty()) {
            return 0;
        } else {
            return this.recordScores.stream().sorted().findFirst().get();
            //return this.recordScores.get(this.recordScores.size() - 1);
        }
    }

    /**
     * Get if the new score is a new highest record.
     * @return true if the new score is a new highest record.
     */
    public boolean isNewRecordScore() {
        return this.newRecordScore;
    }

    /**
     * Get the playing consecutively record score.
     * @return the playing record score
     */
    public int getPlayingRecordScore() {
        return Records.playingRecordScore;
    }

    /**
     * Get if the new score is a new playing consecutively record.
     * @return true if the new score is a new playing consecutively record.
     */
    public boolean isNewPlayingRecordScore() {
        return Records.newPlayingRecordScore;
    }

    /**
     * Orders the deletion of the record file.
     */
    public void clear() {
        this.writer.clear();
    }
}
