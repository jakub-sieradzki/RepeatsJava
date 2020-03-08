package com.rootekstudio.repeatsandroid;

public class RepeatsListDB {
    public String Title;
    public String TableName;
    public String CreateDate;
    public String IsEnabled;
    public String Avatar;
    public String IgnoreChars;
    public String firstLanguage;
    public String secondLanguage;
    public int goodAnswers;
    public int wrongAnswers;
    public int allAnswers;

    public RepeatsListDB() {
    }

    public RepeatsListDB(String Title, String TableName, String CreateDate, String IsEnabled, String Avatar, String IgnoreChars, String firstLanguage, String secondLanguage) {
        this.Title = Title;
        this.TableName = TableName;
        this.CreateDate = CreateDate;
        this.IsEnabled = IsEnabled;
        this.Avatar = Avatar;
        this.IgnoreChars = IgnoreChars;
        this.firstLanguage = firstLanguage;
        this.secondLanguage = secondLanguage;
        this.goodAnswers = 0;
        this.wrongAnswers = 0;
        this.allAnswers = 0;
    }

    public String getitle() {
        return Title;
    }

    public String getTableName() {
        return TableName;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public String getIsEnabled() {
        return IsEnabled;
    }

    public String getAvatar() {
        return Avatar;
    }

    public String getIgnoreChars() {
        return IgnoreChars;
    }

    public String getFirstLanguage() {return firstLanguage;}

    public String getSecondLanguage() {return secondLanguage;}

    public int getGoodAnswers() {return goodAnswers;}

    public int getWrongAnswers() {return wrongAnswers;}

    public int getAllAnswers() {return allAnswers;}

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public void setTableName(String TableName) {
        this.TableName = TableName;
    }

    public void setCreateDate(String CreateDate) {
        this.CreateDate = CreateDate;
    }

    public void setIsEnabled(String IsEnabled) {
        this.IsEnabled = IsEnabled;
    }

    public void setAvatar(String Avatar) {
        this.Avatar = Avatar;
    }

    public void setIgnoreChars(String IgnoreChars) {
        this.IgnoreChars = IgnoreChars;
    }

    public void setFirstLanguage(String firstLanguage) {this.firstLanguage = firstLanguage;}

    public void setSecondLanguage(String secondLanguage) {this.secondLanguage = secondLanguage;}

    public void setGoodAnswers(int goodAnswers) {this.goodAnswers = goodAnswers;}

    public void setWrongAnswers(int wrongAnswers) {this.wrongAnswers = wrongAnswers;}

    public void setAllAnswers(int allAnswers) {this.allAnswers = allAnswers;}
}
