import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A desktop multiple-choice flashcard application for Information Security.
 * The nested classes separate the home page, question form, quiz, and file storage.
 * @author Karabash
  */
public class FlashcardGUI extends JFrame {
    private static final Path CARD_FILE = Path.of("flashcardsA7010E.tsv");
    private static final String SEP = "\t";

    private static final Color BG = new Color(245, 247, 250);
    private static final Color CARD = Color.WHITE;
    private static final Color PRIMARY = new Color(48, 94, 168);
    private static final Color PRIMARY_DARK = new Color(37, 73, 130);
    private static final Color TEXT = new Color(35, 39, 47);
    private static final Color MUTED = new Color(102, 112, 125);
    private static final Color BORDER = new Color(215, 220, 228);

    private final List<Flashcard> cards = FlashcardStorage.load();
    private final CardLayout layout = new CardLayout();
    private final JPanel screens = new JPanel(layout);
    private final HomePanel homePanel = new HomePanel();
    private final AddQuestionPanel addQuestionPanel = new AddQuestionPanel();
    private final QuizPanel quizPanel = new QuizPanel();

    /** Creates the application window and registers its inner-screen classes. */
    public FlashcardGUI() {
        super(FlashcardTitle.fromFile(CARD_FILE));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 650));
        setSize(1050, 760);
        setLocationRelativeTo(null);

        screens.setBackground(BG);
        screens.add(homePanel, "HOME");
        screens.add(addQuestionPanel, "ADD");
        screens.add(quizPanel, "QUIZ");
        add(screens);
        layout.show(screens, "HOME");
    }

    /** Stores one multiple-choice question. */
    private static final class Flashcard {
        final String question;
        final List<String> answers;
        final int correctAnswer;

        Flashcard(String question, List<String> answers, int correctAnswer) {
            this.question = question;
            this.answers = answers;
            this.correctAnswer = correctAnswer;
        }
    }

    /** Home screen inner class. */
    private final class HomePanel extends JPanel {
        private final JLabel count = new JLabel();

        HomePanel() {
            setBackground(BG);
            setLayout(new GridBagLayout());

            JPanel card = createCardPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(new EmptyBorder(45, 65, 45, 65));
            card.setPreferredSize(new Dimension(650, 500));

            JLabel title = new JLabel(FlashcardTitle.fromFile(CARD_FILE));
            title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
            title.setForeground(TEXT);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel subtitle = new JLabel("Practise multiple-choice questions and track your score");
            subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 17));
            subtitle.setForeground(MUTED);
            subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

            count.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
            count.setForeground(PRIMARY);
            count.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton start = createPrimaryButton("Start Quiz", e -> {
                quizPanel.start();
                layout.show(screens, "QUIZ");
            });
            JButton add = createSecondaryButton("Add a Question", e -> layout.show(screens, "ADD"));
            JButton reload = createSecondaryButton("Reload Questions", e -> {
                cards.clear();
                cards.addAll(FlashcardStorage.load());
                updateCount();
                JOptionPane.showMessageDialog(FlashcardGUI.this,
                        "Reloaded " + cards.size() + " questions.",
                        "Questions Reloaded",
                        JOptionPane.INFORMATION_MESSAGE);
            });

            card.add(title);
            card.add(Box.createVerticalStrut(10));
            card.add(subtitle);
            card.add(Box.createVerticalStrut(24));
            card.add(count);
            card.add(Box.createVerticalStrut(35));
            card.add(start);
            card.add(Box.createVerticalStrut(14));
            card.add(add);
            card.add(Box.createVerticalStrut(14));
            card.add(reload);
            card.add(Box.createVerticalGlue());

            JLabel footer = new JLabel("Study at your own pace — you are very welcome to use this app.");
            footer.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            footer.setForeground(MUTED);
            footer.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(footer);

            add(card);
            updateCount();
        }

        void updateCount() {
            count.setText(cards.size() + " questions available");
        }
    }

    /** Question-entry form inner class. */
    private final class AddQuestionPanel extends JPanel {
        private final JTextField question = new JTextField();
        private final JTextField[] answers = {
                new JTextField(), new JTextField(), new JTextField(), new JTextField()
        };
        private final JRadioButton[] correct = {
                new JRadioButton("Correct"), new JRadioButton("Correct"),
                new JRadioButton("Correct"), new JRadioButton("Correct")
        };

        AddQuestionPanel() {
            setBackground(BG);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(35, 55, 35, 55));

            JPanel card = createCardPanel();
            card.setLayout(new BorderLayout(20, 20));
            card.setBorder(new EmptyBorder(30, 35, 30, 35));

            JPanel header = new JPanel();
            header.setOpaque(false);
            header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

            JLabel title = new JLabel("Add a Question");
            title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
            title.setForeground(TEXT);
            title.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel subtitle = new JLabel("Enter one question, four choices, and select the correct answer.");
            subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
            subtitle.setForeground(MUTED);
            subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

            header.add(title);
            header.add(Box.createVerticalStrut(6));
            header.add(subtitle);
            card.add(header, BorderLayout.NORTH);

            JPanel form = new JPanel(new GridBagLayout());
            form.setOpaque(false);
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(8, 6, 8, 6);
            c.fill = GridBagConstraints.HORIZONTAL;

            styleTextField(question);
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 0;
            form.add(createFieldLabel("Question"), c);
            c.gridx = 1;
            c.weightx = 1;
            c.gridwidth = 2;
            form.add(question, c);
            c.gridwidth = 1;

            ButtonGroup group = new ButtonGroup();
            for (int i = 0; i < 4; i++) {
                styleTextField(answers[i]);
                correct[i].setOpaque(false);
                correct[i].setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
                correct[i].setForeground(TEXT);
                group.add(correct[i]);

                c.gridy = i + 1;
                c.gridx = 0;
                c.weightx = 0;
                form.add(createFieldLabel("Choice " + (i + 1)), c);
                c.gridx = 1;
                c.weightx = 1;
                form.add(answers[i], c);
                c.gridx = 2;
                c.weightx = 0;
                form.add(correct[i], c);
            }
            correct[0].setSelected(true);
            card.add(form, BorderLayout.CENTER);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
            actions.setOpaque(false);
            actions.add(createSecondaryButton("Back", e -> layout.show(screens, "HOME")));
            actions.add(createPrimaryButton("Save Question", e -> saveQuestion()));
            card.add(actions, BorderLayout.SOUTH);

            add(card, BorderLayout.CENTER);
        }

        private void saveQuestion() {
            List<String> choices = new ArrayList<>();
            int correctIndex = 0;
            for (int i = 0; i < 4; i++) {
                choices.add(answers[i].getText().trim());
                if (correct[i].isSelected()) correctIndex = i;
            }

            if (question.getText().trim().isEmpty()
                    || choices.stream().anyMatch(String::isEmpty)
                    || question.getText().contains(SEP)
                    || choices.stream().anyMatch(choice -> choice.contains(SEP))) {
                JOptionPane.showMessageDialog(FlashcardGUI.this,
                        "Fill in every field. Tabs are not allowed.",
                        "Cannot Save",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            cards.add(new Flashcard(question.getText().trim(), choices, correctIndex));
            FlashcardStorage.save(cards);
            question.setText("");
            for (JTextField answer : answers) answer.setText("");
            correct[0].setSelected(true);
            homePanel.updateCount();
            JOptionPane.showMessageDialog(FlashcardGUI.this,
                    "Question saved to " + CARD_FILE.getFileName() + ".",
                    "Saved",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /** Quiz-screen inner class. */
    private final class QuizPanel extends JPanel {
        private final JLabel progress = new JLabel();
        private final JLabel scoreLabel = new JLabel();
        private final JLabel question = new JLabel("", SwingConstants.CENTER);
        private final JButton[] answerButtons = {
                new JButton(), new JButton(), new JButton(), new JButton()
        };
        private List<Flashcard> quizCards = new ArrayList<>();
        private int currentQuestion;
        private int score;
        private String previousFirstQuestion = "";

        QuizPanel() {
            setBackground(BG);
            setLayout(new BorderLayout(20, 20));
            setBorder(new EmptyBorder(28, 42, 28, 42));

            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);

            JButton back = createSmallButton("End Quiz", e -> layout.show(screens, "HOME"));
            top.add(back, BorderLayout.WEST);

            progress.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            progress.setForeground(MUTED);
            progress.setHorizontalAlignment(SwingConstants.CENTER);
            top.add(progress, BorderLayout.CENTER);

            scoreLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            scoreLabel.setForeground(PRIMARY);
            scoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            top.add(scoreLabel, BorderLayout.EAST);

            add(top, BorderLayout.NORTH);

            JPanel content = new JPanel(new BorderLayout(20, 20));
            content.setOpaque(false);

            JPanel questionCard = createCardPanel();
            questionCard.setLayout(new GridBagLayout());
            questionCard.setBorder(new EmptyBorder(28, 35, 28, 35));
            questionCard.setPreferredSize(new Dimension(800, 150));

            question.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
            question.setForeground(TEXT);
            question.setVerticalAlignment(SwingConstants.CENTER);
            questionCard.add(question);
            content.add(questionCard, BorderLayout.NORTH);

            JPanel choices = new JPanel(new GridLayout(4, 1, 14, 14));
            choices.setOpaque(false);

            for (int i = 0; i < 4; i++) {
                final int selectedAnswer = i;
                JButton button = answerButtons[i];
                button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
                button.setForeground(TEXT);
                button.setBackground(CARD);
                button.setFocusPainted(false);
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                button.setHorizontalAlignment(SwingConstants.LEFT);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1),
                        new EmptyBorder(14, 22, 14, 22)
                ));
                button.addActionListener(e -> checkAnswer(selectedAnswer));
                choices.add(button);
            }

            content.add(choices, BorderLayout.CENTER);
            add(content, BorderLayout.CENTER);
        }

        void start() {
            if (cards.isEmpty()) {
                JOptionPane.showMessageDialog(FlashcardGUI.this, "There are no questions yet.");
                layout.show(screens, "HOME");
                return;
            }

            quizCards = new ArrayList<>(cards);
            Collections.shuffle(quizCards);
            if (quizCards.size() > 1 && quizCards.get(0).question.equals(previousFirstQuestion)) {
                Collections.swap(quizCards, 0, 1);
            }
            previousFirstQuestion = quizCards.get(0).question;
            currentQuestion = 0;
            score = 0;
            showQuestion();
        }

        private void showQuestion() {
            Flashcard card = quizCards.get(currentQuestion);
            progress.setText("Question " + (currentQuestion + 1) + " of " + quizCards.size());
            scoreLabel.setText("Score: " + score);
            question.setText("<html><div style='text-align:center; width:760px;'>"
                    + card.question + "</div></html>");

            for (int i = 0; i < 4; i++) {
                answerButtons[i].setText("<html><div style='width:760px;'>"
                        + (i + 1) + ".&nbsp;&nbsp;" + card.answers.get(i)
                        + "</div></html>");
            }
        }

        private void checkAnswer(int selectedAnswer) {
            Flashcard card = quizCards.get(currentQuestion);

            if (selectedAnswer == card.correctAnswer) {
                score++;
                JOptionPane.showMessageDialog(FlashcardGUI.this,
                        "Correct!",
                        "Answer",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(FlashcardGUI.this,
                        "Correct answer: " + card.answers.get(card.correctAnswer),
                        "Answer",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            currentQuestion++;
            if (currentQuestion == quizCards.size()) {
                JOptionPane.showMessageDialog(FlashcardGUI.this,
                        "Quiz complete!\nYour score: " + score + " / " + quizCards.size(),
                        "Quiz Complete",
                        JOptionPane.INFORMATION_MESSAGE);
                homePanel.updateCount();
                layout.show(screens, "HOME");
            } else {
                showQuestion();
            }
        }
    }

    /**
     * Utility class for reading the flashcard deck title from the TSV file.
     *
     * <p>The first line may contain deck metadata in this format:
     * {@code #title<TAB>Course title}. If no title is present, the TSV
     * filename without the extension is used as a fallback.</p>
     */
    private static final class FlashcardTitle {

        /** Prevents creation of utility-class objects. */
        private FlashcardTitle() {
        }

        /**
         * Returns the title defined by the selected TSV file.
         *
         * @param file path to the flashcard TSV file
         * @return deck title, or the filename if no title metadata exists
         */
        static String fromFile(Path file) {
            if (Files.exists(file)) {
                try (BufferedReader reader = Files.newBufferedReader(file)) {
                    String firstLine = reader.readLine();

                    if (firstLine != null && firstLine.startsWith("#title\t")) {
                        String title = firstLine.substring(7).trim();
                        if (!title.isEmpty()) {
                            return title;
                        }
                    }
                } catch (IOException ignored) {
                    // Fall back to the filename if the title cannot be read.
                }
            }

            return file.getFileName()
                    .toString()
                    .replaceFirst("(?i)\\.tsv$", "");
        }
    }

    /** File-storage inner class for reading and writing the shared TSV question bank. */
    private static final class FlashcardStorage {
        static List<Flashcard> load() {
            List<Flashcard> result = new ArrayList<>();
            if (!Files.exists(CARD_FILE)) return result;

            try (BufferedReader reader = Files.newBufferedReader(CARD_FILE)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split(SEP, -1);
                    if (fields.length == 6) {
                        result.add(new Flashcard(
                                fields[0],
                                List.of(fields[2], fields[3], fields[4], fields[5]),
                                Integer.parseInt(fields[1])
                        ));
                    }
                }
            } catch (IOException | NumberFormatException e) {
                JOptionPane.showMessageDialog(null,
                        "Could not read questions: " + e.getMessage());
            }
            return result;
        }

        /**
         * Saves all flashcards while preserving the deck title.
         *
         * @param cards flashcards to write to the selected TSV file
         */
        static void save(List<Flashcard> cards) {
            String title = FlashcardTitle.fromFile(CARD_FILE);

            try (BufferedWriter writer = Files.newBufferedWriter(
                    CARD_FILE,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {

                // Write the deck title as metadata before the questions.
                writer.write("#title");
                writer.write(SEP);
                writer.write(title);
                writer.newLine();

                for (Flashcard card : cards) {
                    writer.write(card.question);
                    writer.write(SEP);
                    writer.write(Integer.toString(card.correctAnswer));

                    for (String answer : card.answers) {
                        writer.write(SEP);
                        writer.write(answer);
                    }

                    writer.newLine();
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Could not save questions: " + e.getMessage());
            }
        }
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        return panel;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        label.setForeground(TEXT);
        return label;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        field.setPreferredSize(new Dimension(300, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private JButton createPrimaryButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(320, 52));
        button.setPreferredSize(new Dimension(220, 48));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBorder(new EmptyBorder(12, 22, 12, 22));
        button.addActionListener(listener);
        return button;
    }

    private JButton createSecondaryButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        button.setForeground(PRIMARY_DARK);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(320, 50));
        button.setPreferredSize(new Dimension(220, 46));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(10, 20, 10, 20)
        ));
        button.addActionListener(listener);
        return button;
    }

    private JButton createSmallButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        button.setForeground(PRIMARY_DARK);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 14, 8, 14)
        ));
        button.addActionListener(listener);
        return button;
    }

    /** Launches the application on Swing's event-dispatch thread. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored) {
                // Use the platform default if Nimbus is unavailable.
            }

            new FlashcardGUI().setVisible(true);
        });
    }
}
