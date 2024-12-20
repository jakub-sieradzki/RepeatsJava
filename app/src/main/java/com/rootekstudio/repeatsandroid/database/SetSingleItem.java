package com.rootekstudio.repeatsandroid.database;

public class SetSingleItem {
    private int itemID;
    private String setID;
    private String setName;
    private String Question;
    private String Answer;
    private String Image;
    private int goodAnswers;
    private int wrongAnswers;

    public SetSingleItem() {
    }

    public SetSingleItem(int itemID, String Question, String Answer, String Imag, int goodAnswers, int wrongAnswers) {
        this.itemID = itemID;
        this.Question = Question;
        this.Answer = Answer;
        this.Image = Imag;
        this.goodAnswers = goodAnswers;
        this.wrongAnswers = wrongAnswers;
    }

    public int getItemID() {
        return itemID;
    }

    public String getQuestion() {
        return Question;
    }

    public String getAnswer() {
        return Answer;
    }

    public String getImage() {
        return Image;
    }

    public int getGoodAnswers() {
        return goodAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public int getAllAnswers() {
        return goodAnswers + wrongAnswers;
    }

    public String getSetID() {
        return setID;
    }

    public String getSetName() {
        return setName;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public void setQuestion(String Question) {
        this.Question = Question;
    }

    public void setAnswer(String Answer) {
        this.Answer = Answer;
    }

    public void setImage(String Image) {
        this.Image = Image;
    }

    public void setGoodAnswers(int goodAnswers) {
        this.goodAnswers = goodAnswers;
    }

    public void setWrongAnswers(int wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public void setSetID(String setID) {
        this.setID = setID;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }
}
