import services.funkos.FunkoStorageImpl;

public class Main {
    public static void main(String[] args) {
        FunkoStorageImpl funkoStorage = FunkoStorageImpl.getInstance();
        funkoStorage.loadCsv().subscribe(System.out::println);
    }
}
