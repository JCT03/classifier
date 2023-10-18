package classifier;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClassifierController {
    private MarkovLetters letterChain = new MarkovLetters();
    private MarkovLetters inputLetterChain = new MarkovLetters();
    private MarkovLetterCategory letterCategoryChain = new MarkovLetterCategory();
    private MarkovLetterCategory inputLetterCategoryChain = new MarkovLetterCategory();
    @FXML
    private Label letterStudent;
    @FXML
    private Label letterLLM;
    @FXML
    private Label typeStudent;
    @FXML
    private Label typeLLM;
    @FXML
    private TextArea essayInput;
    @FXML
    private Label letterReferenceMatrix;
    @FXML
    private Label typeReferenceMatrix;
    @FXML
    private Label letterInputMatrix;
    @FXML
    private Label typeInputMatrix;

    @FXML
    public void initialize() throws IOException {
        letterChain.countFrom(new File("src/classifier/text/llm.txt"), "LLM");
        letterChain.countFrom(new File("src/classifier/text/student.txt"), "Student");
        letterCategoryChain.countFrom(new File("src/classifier/text/llm.txt"), "LLM");
        letterCategoryChain.countFrom(new File("src/classifier/text/student.txt"), "Student");
        letterReferenceMatrix.setText(letterChain.toString());
        typeReferenceMatrix.setText(letterCategoryChain.toString());

    }

    @FXML
    public void evaluate() throws IOException {
        ArrayList<Character> chars = MarkovLetters.usableCharacters(essayInput.getText());
        ArrayList<Character> types = MarkovLetterCategory.vowelConsonants(essayInput.getText());
        LinkedHashMap<String, Double> letterDistro = letterChain.labelDistribution(chars);
        LinkedHashMap<String, Double> typeDistro = letterCategoryChain.labelDistribution(types);
        letterStudent.setText("Student: " + letterDistro.get("Student")*100 + "%");
        letterLLM.setText("LLM: " + letterDistro.get("LLM")*100 + "%");
        typeStudent.setText("Student: " + typeDistro.get("Student")*100 + "%");
        typeLLM.setText("LLM: " + typeDistro.get("LLM")*100 + "%");
        Path inputPath = Paths.get("src/classifier/text/input.txt");
        byte[] inputByte = essayInput.getText().getBytes();
        Files.write(inputPath,inputByte);
        inputLetterChain.countFrom(new File("src/classifier/text/input.txt"), "Input");
        inputLetterCategoryChain.countFrom(new File("src/classifier/text/input.txt"), "Input");
        letterInputMatrix.setText(inputLetterChain.toString());
        typeInputMatrix.setText(inputLetterCategoryChain.toString());
    }


}
