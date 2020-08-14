package readability;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Math;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    static double numberOfSentences;
    static double numberOfWords;
    static double numberOfCharacters;
    static double numberOfSyllables;
    static double numberOfPolysyllables;

    public static void main(String[] args) {
        // open input file
        try (Scanner scanner = new Scanner(new File(args[0]))) {

            // this will hold all the text
            StringBuilder textBuilder = new StringBuilder();
            while (scanner.hasNext()) {
                textBuilder.append(scanner.nextLine());
            }

            // count sentences, words, and characters
            String text = textBuilder.toString();

            String characters = text.replaceAll("[\\s\\n\\t]", "");
            String[] words = text.replaceAll("[,\\.!\\?]", "").split("[\\s\\n\\t]");
            String[] sentences = text.split("[.!?]\\s+");
            numberOfSentences = sentences.length;
            numberOfWords = words.length;
            numberOfCharacters = characters.length();

            // count syllables
            double[] syllablesPerWord = new double[words.length];
            for (int i = 0; i < words.length; i++) {
                int vowelCount = 0;
                boolean followingVowel = false;  // we want to avoid counting double-vowels. toggle in loop
                for (int j = 0; j < words[i].length(); j++) {
                    if ("aeiouyAEIOUY".indexOf(words[i].charAt(j)) != -1 && !followingVowel) {
                        if (!(j == words[i].length() - 1 && (words[i].charAt(j) == 'e' || words[i].charAt(j) == 'E'))) { // don't count trailing E/e
                            vowelCount++;
                            followingVowel = true;
                        }
                    } else {
                        followingVowel = false;
                    }
                }
                if (vowelCount == 0) {
                    vowelCount = 1;
                }
                // finally assign the number of syllables
                syllablesPerWord[i] = vowelCount;
            }

            // calculate total numbers of syllables and polysyllables
            numberOfSyllables = Arrays.stream(syllablesPerWord).sum();
            numberOfPolysyllables = 0;
            for (double x : syllablesPerWord) {
                if (x >= 3.0) {
                    numberOfPolysyllables++;
                }
            }

            // print our findings with score and age range
            System.out.println("Words: " + (int)numberOfWords);
            System.out.println("Sentences: " + (int)numberOfSentences);
            System.out.println("Characters: " + (int)numberOfCharacters);
            System.out.println("Syllables: " + numberOfSyllables);
            System.out.println("Polysyllables: " + numberOfPolysyllables);

            // prompt for the desired score
            Scanner scanner1 = new Scanner(System.in);
            System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
            String scoreType = scanner1.nextLine();

            if (scoreType.equalsIgnoreCase("ARI")) {
                ARI(numberOfSentences, numberOfWords, numberOfCharacters);
            } else if (scoreType.equalsIgnoreCase("FK")) {
                FK(numberOfSentences, numberOfWords, numberOfSyllables);
            } else if (scoreType.equalsIgnoreCase("SMOG")) {
                SMOG(numberOfPolysyllables, numberOfSentences);
            } else if (scoreType.equalsIgnoreCase("CL")) {
                CL(numberOfSentences, numberOfWords, numberOfCharacters);
            } else if (scoreType.equalsIgnoreCase("all")) {
                all(numberOfCharacters, numberOfSyllables, numberOfWords, numberOfPolysyllables, numberOfSentences);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static double ARI(double numSentences, double numWords, double numChars) {

        double score = 4.71 * (numChars / numWords) + 0.5 * (numWords / numSentences) - 21.43;
        System.out.println("Automated Readability Index: " + (Math.round(score * 100.0) / 100.0) + " (about "
                + suggestedAgeRange(score) + " year olds).");
        return suggestedAgeRange(score);
    }

    public static double FK(double numSentences, double numWords, double numSyllables) {

        double score = 0.39 * (numWords / numSentences) + 11.8 * (numSyllables / numWords) - 15.59;
        System.out.println("Flesch–Kincaid readability tests: " + (Math.round(score * 100.0) / 100.0) + " (about "
                + suggestedAgeRange(score) + " year olds).");
        return suggestedAgeRange(score);
    }

    public static double SMOG(double numPolysyllables, double numSentences) {

        double score = 1.043 * Math.sqrt(numPolysyllables * (30 / numSentences)) + 3.1291;
        System.out.println("Simple Measure of Gobbledygook: " + (Math.round(score * 100.0) / 100.0) + " (about "
                + suggestedAgeRange(score) + " year olds).");
        return suggestedAgeRange(score);
    }

    public static double CL(double numSentences, double numWords, double numCharacters) {

        double L = (numCharacters / numWords) * 100;
        double S = (numSentences / numWords) * 100;
        double score = 0.0588 * L - 0.296 * S - 15.8;
        System.out.println("Coleman–Liau index: " + (Math.round(score * 100.0) / 100.0) + " (about "
                + suggestedAgeRange(score) + " year olds).");
        return suggestedAgeRange(score);
    }

    public static void all(double numCharacters, double numSyllables, double numWords, double numPolysyllables,
                           double numSentences) {

        double a = ARI(numSentences, numWords, numCharacters);
        double b = FK(numSentences, numWords, numSyllables);
        double c = SMOG(numPolysyllables, numSentences);
        double d = CL(numSentences, numWords, numCharacters);

        System.out.println();
        System.out.println("This text should be understood in average by " + ((a+b+c+d) / 4.0) + " year olds.");
    }

    public static int suggestedAgeRange(double score) {

        int roundScore = (int)Math.ceil(score);
        switch (roundScore) {
            case 1:
                return 6;
            case 2:
                return 7;
            case 3:
                return 9;
            case 4:
                return 10;
            case 5:
                return 11;
            case 6:
                return 12;
            case 7:
                return 13;
            case 8:
                return 14;
            case 9:
                return 15;
            case 10:
                return 16;
            case 11:
                return 17;
            case 12:
                return 18;
            case 13:
                return 24;
            case 14:
                return 25;
        }
        return -1;
    }
}