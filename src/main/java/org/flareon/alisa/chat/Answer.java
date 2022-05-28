package org.flareon.alisa.chat;

import org.bukkit.entity.Player;
import org.flareon.alisa.FLAlisa;
import org.flareon.alisa.utils.FileUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Answer {

    public enum AnswerReason
    {
        CAPS,
        PROFANITY,
        FLOOD,
        ADVERTISEMENT,
        WARN,
    }
    private static Random rand;
    public HashMap<AnswerReason, AnswerChunk> answers = new HashMap<>();
    private final ArrayList<String> helloAnswers;
    private final ArrayList<String> byeAnswers;
    private final FLAlisa ALISA;
    public Answer() {
        ALISA = FLAlisa.getInstance();
        rand = new Random();
        this.createAnswers();

        this.helloAnswers = FileUtil.readProjectFileLines("hello-answers.txt");
        this.byeAnswers = FileUtil.readProjectFileLines("bye-answers.txt");
    }

    private void createAnswers() {
        this.answers.put(AnswerReason.CAPS, new AnswerChunk("answers-caps.txt"));
        this.answers.put(AnswerReason.FLOOD, new AnswerChunk("answers-flood.txt"));
        this.answers.put(AnswerReason.ADVERTISEMENT, new AnswerChunk("answers-advertisement.txt"));
        this.answers.put(AnswerReason.PROFANITY, new AnswerChunk("answers-profanity.txt"));
        this.answers.put(AnswerReason.WARN, new AnswerChunk("answers-warn.txt"));
    }

    public void sayHello(final Player player) {
        ALISA.say(helloAnswers.get(rand.nextInt(helloAnswers.size())), player);
    }
    public void sayBye(final Player player) {
        ALISA.say(byeAnswers.get(rand.nextInt(byeAnswers.size())), player);
    }

    public static class AnswerChunk {
        private final ArrayList<String> answerStrings;
        private AnswerChunk(final String answersFilePath) {
            this.answerStrings = FileUtil.readProjectFileLines(answersFilePath);
        }
        public String getRandomAnswer(final String playerName) {
            return String.format(answerStrings.get(rand.nextInt(answerStrings.size())), "#c2" + playerName + "#c1");
        }
    }
}