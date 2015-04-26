package math.model;

public class MatchCountConditional implements Conditional<Boolean> {

    private int pivotRecordID;
    private int howMany;
    
    public MatchCountConditional(int pivotRecordID, int howMany) {
        this.pivotRecordID = pivotRecordID;
        this.howMany = howMany;
    }
    
    @Override
    public Boolean check(int recordID) {
        return recordID > pivotRecordID - howMany;
    }

    
}
