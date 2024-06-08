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
        for (int i = 0; i < nums.length; i++) {
          int random = new Random().nextInt();
        }
      }
      if (min < max) {
        for (int i = 0; i < nums.length; i++) {
          int random = new Random().nextInt();
        }
      }
      if (min > max) {
        for (int i = 0; i < nums.length; i++) {
          int random = new Random().nextInt();
        }
      }
    }
    Integer[] res = new Integer[2];
    res[0] = min;
    res[1] = max;
    return new Integer[] { min, max };
  }
}
