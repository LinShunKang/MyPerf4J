package cn.myperf4j.base.util;

import cn.myperf4j.base.util.collections.ListUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * Created by LinShunkang on 2020/05/24
 */
public class ListUtilsTest {

    @Test
    public void testPartition() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        List<List<Integer>> partition = ListUtils.partition(list, 2);
        Assert.assertEquals(5, partition.size());
        Assert.assertEquals(Arrays.asList(1, 2), partition.get(0));
        Assert.assertEquals(Arrays.asList(3, 4), partition.get(1));
        Assert.assertEquals(Arrays.asList(5, 6), partition.get(2));
        Assert.assertEquals(Arrays.asList(7, 8), partition.get(3));
        Assert.assertEquals(singletonList(9), partition.get(4));

        List<Integer> list2 = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        List<List<Integer>> partition2 = ListUtils.partition(list2, 2);
        Assert.assertEquals(4, partition2.size());
        Assert.assertEquals(2, partition2.get(0).size());
        Assert.assertEquals(2, partition2.get(1).size());
        Assert.assertEquals(2, partition2.get(2).size());
        Assert.assertEquals(2, partition2.get(3).size());

        List<Integer> list3 = new ArrayList<>(0);
        List<List<Integer>> partition3 = ListUtils.partition(list3, 12);
        Assert.assertEquals(0, partition3.size());

        List<List<Integer>> partition4 = ListUtils.partition(null, 12);
        Assert.assertEquals(0, partition4.size());
    }

    @Test
    public void testEmpty() {
        Assert.assertTrue(ListUtils.isEmpty(null));
        Assert.assertTrue(ListUtils.isEmpty(new ArrayList<>()));
        Assert.assertTrue(ListUtils.isEmpty(new ArrayList<>(1)));
        Assert.assertFalse(ListUtils.isEmpty(Arrays.asList(1, 2, 3)));

        Assert.assertFalse(ListUtils.isNotEmpty(null));
        Assert.assertFalse(ListUtils.isNotEmpty(new ArrayList<>()));
        Assert.assertFalse(ListUtils.isNotEmpty(new ArrayList<>(1)));
        Assert.assertTrue(ListUtils.isNotEmpty(Arrays.asList(1, 2, 3)));
    }
}
