package DnDCharacterSheet;

import java.util.ArrayList;

public class CharacterSheet {
    private String name;
    private ArrayList<Profession> professions;
    private Profession profession;
    private ArrayList<Race> races;
    private Race race;
    private String info;
    private String story;
    private int hp;
    private Skill[] skills;
    private Stats stats;
    private WizText wizText;
    private String inventory;
    private String spellSheet;
    //private SavingThrow[] savingThrows;
    private int[] savingThrows;
    private MiscStats miscStats;
    private int exp;
    private ArrayList<Feature> features;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Profession> getProfessions() {
        return professions;
    }

    public void setProfessions(ArrayList<Profession> professions) {
        this.professions = professions;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public Skill[] getSkills() {
        return skills;
    }

    public void setSkills(Skill[] skills) {
        this.skills = skills;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public WizText getWizText() {
        return wizText;
    }

    public void setWizText(WizText wizText) {
        this.wizText = wizText;
    }

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }

    public String getSpellSheet() {
        return spellSheet;
    }

    public void setSpellSheet(String spellSheet) {
        this.spellSheet = spellSheet;
    }

    public int[] getSavingThrows() {
        calcSavingThrows();
        return savingThrows;
    }

    public void calcSavingThrows()
    {
        savingThrows = new int[]{0,0,0,0,0,0};
        for(int i = 0; i < 6; i++)
        {
            savingThrows[i] = stats.getStat(i,1);
            if((profession.getSavingThrows())[i] == 1)
            {
                savingThrows[i] = savingThrows[i] + miscStats.getProfBonus();
            }
        }
    }

    public MiscStats getMiscStats() {
        return miscStats;
    }

    public void setMiscStats(MiscStats miscStats) {
        this.miscStats = miscStats;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public ArrayList<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(ArrayList<Feature> features) {
        this.features = features;
    }
}
