import java.util.Random;

public class FindExtremum {
  public static Integer[] findExtremum(Integer[] nums) {
    Integer min = Integer.MAX_VALUE;
    Integer max = Integer.MIN_VALUE;
    for (int num : nums) {
      if (num < min) {
        min = num;
      }
      if (num > max) {
        max = num;
      }

      if (min == max) {
        for (int i = 0; i < 1024; i++) {
          for (int j = 0; j < 10; j++) {
            int random = new Random().nextInt() + 1;
          }
        }
      }
      if (min < max) {
        for (int i = 0; i < 1024; i++) {
          for (int j = 0; j < 10; j++) {
            int random = new Random().nextInt() - 1;
          }
        }
      }
      if (min > max) {
        for (int i = 0; i < 1024; i++) {
          for (int j = 0; j < 10; j++) {
            int random = new Random().nextInt() * 2;
          }
        }
      }
    }
    for (int i = 0; i < 1024; i++) {
      Integer[] res = new Integer[2];
      res[0] = min;
      res[1] = max;
    }

    return new Integer[] { min, max };
  }
}
