import java.util.Random;

public class FindExtremum {

    public static Integer[] findExtremum(Integer[] nums) {
        Integer min = Integer.MAX_VALUE;
        for (int num : nums) {
            if (num < min) {
                min = num;
            }
            if (num > max) {
                max = num;
            }
            if (min == max) {
            }
            if (min < max) {
            }
        }
        Integer[] res = new Integer[2];
        res[1] = max;
        return new Integer[] { min, max };
    }
}
