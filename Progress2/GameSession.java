import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.File;
import java.util.Arrays;

// Owner: คนที่ 3 (Menu & Game Logic)
public class GameSession {

    private User user;
    private Difficulty difficulty;
    private List<PhotoPair> pairs;

    private int pairIndex; 
    private PhotoPair pair;      
    private List<DifferenceSpot> activeSpots;

    private int foundCount;
    private int roundFound;
    private int misses;
    private int timeRemaining;

    public GameSession(User user, Difficulty difficulty) {
        this.user = user;
        this.difficulty = difficulty;
        this.foundCount = 0;
        this.misses = 0;
        this.pairs = loadPairs(difficulty);
        this.timeRemaining = difficulty.getSeconds();

        setupPair(0);
    }

        private static List<PhotoPair> loadPairs(Difficulty d){
            String level = (d == Difficulty.NORMAL) ? "normal" : d.name().toLowerCase();
            File levelDir = new File("photos/" + level);
            File[] folders = levelDir.listFiles(File::isDirectory);
            if(folders == null || folders.length == 0){
                throw new IllegalStateException("Folder is empty: " + levelDir.getAbsolutePath());
            }
            Arrays.sort(folders);

            List<PhotoPair> result = new ArrayList<>();
            for(File f : folders){
                PhotoPair p = PhotoPair.loadFromFolder(f.getPath());
                if(p != null){
                    result.add(p);
                }
            }
            if(result.isEmpty()){
                throw new IllegalStateException("Loading photo error please check folder " + levelDir.getAbsolutePath());
            }
            return result;
        }

        private void setupPair(int index){
            this.pairIndex = index;
            this.pair = pairs.get(index);
            this.roundFound = 0;

            List<DifferenceSpot> allSpots = new ArrayList<>(pair.getAllSpots());
            Collections.shuffle(allSpots);
            int countToPick = Math.min(difficulty.getSpotCount(), allSpots.size());
            this.activeSpots = new ArrayList<>(allSpots.subList(0, countToPick));
        }

        public boolean hasNextPair(){
            return pairIndex + 1 < pairs.size();
        }

        public void nextPair(){
            if(hasNextPair()){
                setupPair(pairIndex + 1);
            }
        }

        public boolean isFinished(){
            return isRoundComplete() && !hasNextPair();
        }

        public int getPairNumber() {
            return pairIndex + 1 ;
        }
        public int getPairCount() {
            return pairs.size();
        }
    

    public PhotoPair getPair() {
        return pair;
    }

    public List<DifferenceSpot> getActiveSpots() {
        return activeSpots;
    }

    // ตรวจสอบเมื่อมีการคลิก (คนที่ 4 จะเรียกฟังก์ชันนี้จาก GameFrame)
    public boolean handleClick(int x, int y) {
        // วนหาว่าคลิกโดนจุดไหนใน activeSpots ไหม
        for (DifferenceSpot spot : activeSpots) {
            if (spot.contains(x, y)) {
                if (!spot.isFound()) { // ถ้ายังไม่เคยหาเจอ
                    spot.setFound(true);
                    foundCount++;
                    roundFound++;
                    return true; // คลิกโดนและถูกต้อง
                }
                return false; // คลิกโดนจุดที่เคยเจอแล้ว
            }
        }
        // ถ้าวนจบแล้วไม่โดนจุดไหนเลย แสดงว่าพลาด
        misses++;
        return false; 
    }

    public void tick() {
        if (timeRemaining > 0) {
            timeRemaining--;
        }
    }

    public boolean isRoundComplete() {
        // เช็คว่าหาเจอครบตามจำนวนที่ตั้งไว้หรือยัง
        return roundFound >= activeSpots.size();
    }

    public int getFoundCount() { return foundCount; }
    public int getRoundFound() { return roundFound;}
    public int getMisses() { return misses; }
    public int getTimeRemaining() { return timeRemaining; }

    public User getUser(){ return  user; }
    public Difficulty getDifficulty() { return difficulty;}

    public GameResult toResult(){
        int timeUsed = difficulty.getSeconds() - timeRemaining;
        int score = Math.max(0, foundCount * 100 - misses * 10 + timeRemaining);
        return new GameResult(user.getUsername(), difficulty, score, timeUsed);
    }
}
