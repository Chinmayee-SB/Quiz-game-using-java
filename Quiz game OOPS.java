import java.io.*;
import java.util.*;


class OptionOutOfBoundsException extends Exception {
    public OptionOutOfBoundsException(String message) {
        super(message);
    }
}


class Question {
    private String questionText;
    private String[] options;
    private int correctOption;

    public Question(String questionText, String[] options, int correctOption) {
        this.questionText = questionText;
        this.options = options;
        this.correctOption = correctOption;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String[] getOptions() {
        return options;
    }

    public int getCorrectOption() {
        return correctOption;
    }

    
    public static void saveQuestions(Question[] questions, String filename, int questionCount) {
        try ( PrintWriter writer = new PrintWriter(new FileWriter(filename)) ) 
        {
            for (int i = 0; i < questionCount; i++) {
                Question q = questions[i];
                
                writer.print(q.getQuestionText().replace(";", ",") + ";");
                
                for (String option : q.getOptions()) {
                    writer.print(option.replace(";", ",") + ";");
                }
                
                writer.println(q.getCorrectOption());
            }
            
            System.out.println("Questions saved successfully to " + filename);
        } 
        
        catch (IOException e) {
            System.err.println("Error saving questions: " + e.getMessage());
        }
    }

    
    public static QuestionLoadResult loadQuestions(String filename) {
        Question[] questions = new Question[100];
        int questionCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");

                if (parts.length < 6) {
                    System.err.println("Skipping invalid question line: " + line);
                    continue;
                }

                String questionText = parts[0];

                String[] options = new String[4];
                for (int i = 0; i < 4; i++) {
                    options[i] = parts[i + 1];
                }

                int correctOption = Integer.parseInt(parts[5]);

                if (questionCount < questions.length) {
                    questions[questionCount++] = new Question(questionText, options, correctOption);
                } 
                else {
                    System.err.println("it is full , cannot add more questions.");
                }
            }
            System.out.println("Questions loaded successfully from " + filename);
        } 
        
        catch (FileNotFoundException e) {
            System.err.println("File not found: " + filename);
        } 
        
        catch (IOException e) {
            System.err.println("Error loading questions: " + e.getMessage());
        }
        
        return new QuestionLoadResult(questions, questionCount);
    }

    
    public static Question createQuestionInteractively(Scanner scanner) {
        System.out.print("Enter the question text: ");
        scanner.nextLine(); 
        String questionText = scanner.nextLine();
        String[] options = new String[4];
        
        for (int i = 0; i < 4; i++) {
            System.out.print("Enter option " + (i + 1) + ": ");
            options[i] = scanner.nextLine();
        }

        System.out.print("Enter the correct option number (1-4): ");
        int correctOption = scanner.nextInt() - 1;

        return new Question(questionText, options, correctOption);
    }
}


class QuestionLoadResult {
    Question[] questions;
    int questionCount;

    public QuestionLoadResult(Question[] questions, int questionCount) {
        this.questions = questions;
        this.questionCount = questionCount;
    }
}


interface Player {
    String getName();
    int getScore();
    void answerCorrectly();
    void answerIncorrectly();
    void displayScore();
}


class BasicPlayer implements Player {
    private String name;
    private int score;

    public BasicPlayer(String name) {
        this.name = name;
        this.score = 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void answerCorrectly() {
        score += 10;
    }

    @Override
    public void answerIncorrectly() {
        score -= 5;
    }

    @Override
    public void displayScore() {
        System.out.println(name + "'s score: " + score);
    }
}

// Advanced Player implementation
class AdvancedPlayer implements Player {
    private String name;
    private int score;

    public AdvancedPlayer(String name) {
        this.name = name;
        this.score = 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void answerCorrectly() {
        score += 15;
    }

    @Override
    public void answerIncorrectly() {
        score -= 10;
    }

    @Override
    public void displayScore() {
        System.out.println(name + "'s score: " + score);
    }
}


class Quiz {
    private Question[] questions;

    public Quiz(Question[] questions) {
        this.questions = questions;
    }

    public void askQuestion(Question question) {
        System.out.println(question.getQuestionText());
    }

    public void askQuestion(Question question, boolean showOptions) {
        
        System.out.println(question.getQuestionText());
        
        if (showOptions) {
            String[] options = question.getOptions();
            for (int i = 0; i < options.length; i++) {
                System.out.println((i + 1) + ". " + options[i]);
            }
        }
    }

    public boolean checkAnswer(Question question, int answer) throws OptionOutOfBoundsException {
        
        if (answer < 1 || answer > 4) {
            throw new OptionOutOfBoundsException("Invalid option. Please select an option between 1 and 4.");
        }
        return question.getCorrectOption() == (answer - 1); 
    }
}


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String QUESTIONS_FILE = "quiz_questions.txt";
        Question[] questions = new Question[100];
        int questionCount = 0;

        
        while (true) {
            System.out.println("\n--- Quiz Game Menu ---");
            System.out.println("1. Create New Questions");
            System.out.println("2. Load Existing Questions");
            System.out.println("3. Play Quiz");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    
                    QuestionLoadResult loadResult = Question.loadQuestions(QUESTIONS_FILE);
                    questions = loadResult.questions;
                    questionCount = loadResult.questionCount;

                    System.out.print("How many questions do you want to add? ");
                    int numQuestions = scanner.nextInt();
                    
                    for (int i = 0; i < numQuestions; i++) {
                        Question newQuestion = Question.createQuestionInteractively(scanner);
                        if (questionCount < questions.length) {
                            questions[questionCount++] = newQuestion;
                        } else {
                            System.err.println("Cannot add more questions, array is full.");
                        }
                    }
                    
                    Question.saveQuestions(questions, QUESTIONS_FILE, questionCount);
                    break;

                case 2:
                    QuestionLoadResult loadedResult = Question.loadQuestions(QUESTIONS_FILE);
                    questions = loadedResult.questions;
                    questionCount = loadedResult.questionCount;

                    if (questionCount == 0) {
                        System.out.println("No questions found. Please create questions first.");
                        continue;
                    }
                    
                    
                    System.out.println("\n--- Loaded Questions ---");
                    
                    
                    for (int i = 0; i < questionCount; i++) {
                        Question q = questions[i];
                        System.out.println("Question " + (i + 1) + ": " + q.getQuestionText());
                        String[] options = q.getOptions();
                        for (int j = 0; j < options.length; j++) {
                            System.out.println("  " + (j + 1) + ". " + options[j]);
                        }
                        System.out.println("  Correct Option: " + (q.getCorrectOption() + 1));
                        System.out.println(); 
                    }
                    break;

                case 3:
                    QuestionLoadResult quizResult = Question.loadQuestions(QUESTIONS_FILE);
                    questions = quizResult.questions;
                    questionCount = quizResult.questionCount;
                    

                    if (questionCount == 0) {
                        System.out.println("No questions available. Please create questions first.");
                        continue;
                    }

                    
                    Quiz quiz = new Quiz(questions);

          
                    System.out.print("Enter name for Player 1: ");
                    scanner.nextLine(); 
                    String player1Name = scanner.nextLine();
                    System.out.print("Is Player 1 advanced? (yes/no): ");
                    String player1Type = scanner.nextLine();
                    
                    Player player1 = (player1Type=="yes") ? 
                        new AdvancedPlayer(player1Name) : new BasicPlayer(player1Name);

                    
                    System.out.print("Enter name for Player 2: ");
                    String player2Name = scanner.nextLine();
                    System.out.print("Is Player 2 advanced? (yes/no): ");
                    String player2Type = scanner.nextLine();
                    
                    Player player2 = (player2Type=="yes") ? 
                        new AdvancedPlayer(player2Name) : new BasicPlayer(player2Name);

                    
                    for (int i = 0; i < questionCount; i++) {
                        System.out.println("---");
                        quiz.askQuestion(questions[i], true);

                        
                        int player1Answer;
                        while (true) {
                            try {
                                System.out.print(player1.getName() + ", enter your answer (1-4): ");
                                player1Answer = scanner.nextInt();
                                if (quiz.checkAnswer(questions[i], player1Answer)) {
                                    player1.answerCorrectly();
                                } else {
                                    player1.answerIncorrectly();
                                }
                                break;
                            } catch (OptionOutOfBoundsException e) {
                                System.out.println(e.getMessage());
                            }
                        }

                        
                        int player2Answer;
                        while (true) {
                            try {
                                System.out.print(player2.getName() + ", enter your answer (1-4): ");
                                player2Answer = scanner.nextInt();
                                if (quiz.checkAnswer(questions[i], player2Answer)) {
                                    player2.answerCorrectly();
                                } else {
                                    player2.answerIncorrectly();
                                }
                                break;
                            } catch (OptionOutOfBoundsException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    }

                    
                    System.out.println("--- Final Scores ---");
                    player1.displayScore();
                    player2.displayScore();
                    break;

                case 4:
                    System.out.println("Exiting Quiz Game. Goodbye!");
                    scanner.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}