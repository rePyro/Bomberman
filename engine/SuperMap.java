import java.util.ArrayList;
public class SuperMap {
    private Map mainMap;
    private ArrayList<Map> prediction;
    private int vision;

    public SuperMap(Map mainMap) {
        this.mainMap = mainMap;
        this.prediction = new ArrayList<>();
        vision = mainMap.getBombFuse() + mainMap.getFireFuse()+1;
    }
    public Map getMainMap() {
        return mainMap;
    }
    public ArrayList<Map> getPrediction() {
        return prediction;
    }
    public Map getPrediction(int index) {
        if (index >= 0 && index < prediction.size()) {
            return prediction.get(index);
        } else {
            return null; // or throw an exception
        }
    }
    public Map predict(Map map) {
        if (map != null) {
            Map predictedMap = new Map(map);
            predictedMap.gameTick(); // Assuming Map has a copy constructor or clone method
            return predictedMap;
        } else {
            throw new IllegalArgumentException("Map cannot be null");
        }
    }
    public void addClone(Map map) {
        prediction.add(new Map(map));
    }
    public void makePrediction() {
        prediction.clear(); // Clear previous predictions
        if (mainMap == null) {
            throw new IllegalStateException("Main map is not initialized");
        }
        addClone(mainMap); // Assuming Map has a copy constructor or clone method
        for (int i = 0; i < vision; i++) {
            prediction.add(predict(prediction.get(i))); // Predict the next state of the map
        }
    }
    public void printPrediction() {
        for (int i = 0; i < prediction.size(); i++) {
            System.out.println("Prediction " + i + ":");
            prediction.get(i).printMap(); // Assuming Map has a printMap method
        }
    }
}
