# Flashcards App

A simple Java desktop application for practising multiple-choice questions.

The app is **general-purpose**: the Java code does not depend on a specific subject. Questions are loaded from a TSV file, so you can use the same app for different courses by changing the question bank.

## Included Question Banks

This repository currently includes question banks for:

- **Introduction to the Master Program in Information Security A0002E**
- **Applied Computer Security A7010E**

The app always reads the active question bank from:

```text
flashcards.tsv
```

An additional course question bank is included as:

```text
flashcardsA7010E.tsv
```

## Project Files

```text
FlashcardGUI.java
Flashcards.jar
flashcards.tsv
flashcardsA7010E.tsv
Run Flashcards.sh
README.md
```

### What each file does

- `FlashcardGUI.java` — Java source code for the application.
- `Flashcards.jar` — runnable Java version of the application.
- `flashcards.tsv` — active question bank loaded by the app.
- `flashcardsA7010E.tsv` — additional question bank for Applied Computer Security A7010E.
- `Run Flashcards.sh` — Ubuntu/Linux launcher.
- `README.md` — project instructions.

## Requirements

To run the JAR file, install **Java 17 or newer**.

Check your Java version with:

```bash
java -version
```

If you also want to compile the source code, you need a JDK:

```bash
javac -version
```

## Ubuntu / Debian / Linux Mint

Install Java:

```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

Check the installation:

```bash
java -version
javac -version
```

### Run with the Linux launcher

First make the launcher executable:

```bash
chmod +x "Run Flashcards.sh"
```

Then run:

```bash
./"Run Flashcards.sh"
```

The launcher runs `Flashcards.jar` from the same folder, so `flashcards.tsv` can be found correctly.

### Run the JAR directly

```bash
java -jar Flashcards.jar
```

## Windows

Install a Java JDK 17 or newer, such as Eclipse Temurin.

Then open Command Prompt or PowerShell in the project folder and run:

```text
java -jar Flashcards.jar
```

## macOS

Install Java 17 or newer.

Then open Terminal in the project folder and run:

```bash
java -jar Flashcards.jar
```

## Run from Source Code

Compile:

```bash
javac FlashcardGUI.java
```

Run:

```bash
java FlashcardGUI
```

## Build the JAR

If you change `FlashcardGUI.java`, compile it first:

```bash
javac FlashcardGUI.java
```

Then create a new runnable JAR:

```bash
jar --create --file Flashcards.jar --main-class FlashcardGUI *.class
```

Run the new JAR:

```bash
java -jar Flashcards.jar
```

## Switching Question Banks

The application always looks for a file named:

```text
flashcards.tsv
```

To use the Applied Computer Security A7010E question bank, make a copy of it and name the copy `flashcards.tsv`.

On Ubuntu/Linux:

```bash
cp flashcardsA7010E.tsv flashcards.tsv
```

If you want to keep your current `flashcards.tsv`, back it up first:

```bash
cp flashcards.tsv flashcards-backup.tsv
cp flashcardsA7010E.tsv flashcards.tsv
```

Then start the app again.

## Create Your Own Question Bank

You can use the app for any subject.

Create or replace `flashcards.tsv` using this six-column format:

```text
question<TAB>correct-answer-index<TAB>choice-1<TAB>choice-2<TAB>choice-3<TAB>choice-4
```

Example:

```text
What is 2 + 2?	1	3	4	5	6
```

The correct-answer index is **zero-based**:

```text
0 = choice 1
1 = choice 2
2 = choice 3
3 = choice 4
```

In the example above, the correct answer is `4`, which is choice 2, so the stored index is `1`.

### TSV rules

- Each question must have exactly four answer choices.
- Each row must contain exactly six columns.
- Separate columns with TAB characters.
- Do not put TAB characters inside the question or answer text.
- The correct-answer index must be `0`, `1`, `2`, or `3`.

## Using the App

### Start Quiz

Starts a quiz using the questions currently loaded from `flashcards.tsv`.

Questions are shown in random order.

### Add a Question

Enter one question, four answer choices, and select the correct answer.

The app saves the question to `flashcards.tsv`.

### Reload Shared Questions

Reloads the current contents of `flashcards.tsv` without restarting the application.

## Visual Studio Code

Open the project folder in Visual Studio Code.

For Java development, install the **Extension Pack for Java**.

Open:

```text
FlashcardGUI.java
```

You can run the program from VS Code or use the integrated terminal:

```bash
javac FlashcardGUI.java
java FlashcardGUI
```

## GitHub

Clone the repository:

```bash
git clone https://github.com/karabash/flashcards.git
cd flashcards
```

Run the app:

```bash
java -jar Flashcards.jar
```

On Ubuntu/Linux you can also use:

```bash
chmod +x "Run Flashcards.sh"
./"Run Flashcards.sh"
```

## Updating the Repository

After changing files:

```bash
git add .
git commit -m "Update flashcards app"
git push
```

Compiled `.class` files do not need to be committed if you already include the source code and JAR.

## Contributing

You are welcome to use, modify, and improve the app.

You can also create your own TSV question bank for another subject without changing the Java code.

If you contribute questions, check that:

- the question is correct
- all four choices are present
- the correct-answer index is correct
- the row follows the required TSV format

## Note

`flashcards.tsv` must stay in the same working folder as the application when it runs.

If the app starts but shows no questions, check that `flashcards.tsv` exists and contains valid six-column rows.
