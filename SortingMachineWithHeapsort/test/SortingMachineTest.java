import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;

import org.junit.Test;

import components.sortingmachine.SortingMachine;

/**
 * JUnit test fixture for {@code SortingMachine<String>}'s constructor and
 * kernel methods.
 *
 * @author Wenbo Nan
 *
 */
public abstract class SortingMachineTest {

    /**
     * Invokes the appropriate {@code SortingMachine} constructor for the
     * implementation under test and returns the result.
     *
     * @param order
     *            the {@code Comparator} defining the order for {@code String}
     * @return the new {@code SortingMachine}
     * @requires IS_TOTAL_PREORDER([relation computed by order.compare method])
     * @ensures constructorTest = (true, order, {})
     */
    protected abstract SortingMachine<String> constructorTest(
            Comparator<String> order);

    /**
     * Invokes the appropriate {@code SortingMachine} constructor for the
     * reference implementation and returns the result.
     *
     * @param order
     *            the {@code Comparator} defining the order for {@code String}
     * @return the new {@code SortingMachine}
     * @requires IS_TOTAL_PREORDER([relation computed by order.compare method])
     * @ensures constructorRef = (true, order, {})
     */
    protected abstract SortingMachine<String> constructorRef(
            Comparator<String> order);

    /**
     *
     * Creates and returns a {@code SortingMachine<String>} of the
     * implementation under test type with the given entries and mode.
     *
     * @param order
     *            the {@code Comparator} defining the order for {@code String}
     * @param insertionMode
     *            flag indicating the machine mode
     * @param args
     *            the entries for the {@code SortingMachine}
     * @return the constructed {@code SortingMachine}
     * @requires IS_TOTAL_PREORDER([relation computed by order.compare method])
     * @ensures <pre>
     * createFromArgsTest = (insertionMode, order, [multiset of entries in args])
     * </pre>
     */
    private SortingMachine<String> createFromArgsTest(Comparator<String> order,
            boolean insertionMode, String... args) {
        SortingMachine<String> sm = this.constructorTest(order);
        for (int i = 0; i < args.length; i++) {
            sm.add(args[i]);
        }
        if (!insertionMode) {
            sm.changeToExtractionMode();
        }
        return sm;
    }

    /**
     *
     * Creates and returns a {@code SortingMachine<String>} of the reference
     * implementation type with the given entries and mode.
     *
     * @param order
     *            the {@code Comparator} defining the order for {@code String}
     * @param insertionMode
     *            flag indicating the machine mode
     * @param args
     *            the entries for the {@code SortingMachine}
     * @return the constructed {@code SortingMachine}
     * @requires IS_TOTAL_PREORDER([relation computed by order.compare method])
     * @ensures <pre>
     * createFromArgsRef = (insertionMode, order, [multiset of entries in args])
     * </pre>
     */
    private SortingMachine<String> createFromArgsRef(Comparator<String> order,
            boolean insertionMode, String... args) {
        SortingMachine<String> sm = this.constructorRef(order);
        for (int i = 0; i < args.length; i++) {
            sm.add(args[i]);
        }
        if (!insertionMode) {
            sm.changeToExtractionMode();
        }
        return sm;
    }

    /**
     * Comparator<String> implementation to be used in all test cases. Compare
     * {@code String}s in lexicographic order.
     */
    private static class StringLT implements Comparator<String> {

        @Override
        public int compare(String s1, String s2) {
            return s1.compareToIgnoreCase(s2);
        }

    }

    /**
     * Comparator instance to be used in all test cases.
     */
    private static final StringLT ORDER = new StringLT();

    @Test
    public final void testConstructor() {
        SortingMachine<String> m = this.constructorTest(ORDER);
        SortingMachine<String> mExpected = this.constructorRef(ORDER);
        assertEquals(mExpected, m);
    }

    @Test
    public final void testAddEmpty() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true);
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true,
                "green");
        m.add("green");
        assertEquals(mExpected, m);
    }

    @Test
    public final void testAddToOne() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true, "a");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true,
                "green", "a");
        m.add("green");
        assertEquals(mExpected, m);
    }

    @Test
    public final void testAddToMoreThanOne() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true, "a",
                "b", "c");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true,
                "green", "a", "b", "c");
        m.add("green");
        assertEquals(mExpected, m);
    }

    @Test
    public final void testChaneToExtractionMode() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true, "a",
                "b", "c");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, false,
                "a", "b", "c");
        m.changeToExtractionMode();
        assertEquals(mExpected, m);
    }

    @Test
    public final void testRemoveFirstLeavingEmpty() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, false, "a");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, false);
        String str = m.removeFirst();
        assertEquals(mExpected, m);
        assertEquals("a", str);
    }

    @Test
    public final void testRemoveFirstLeavingOne() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, false, "a",
                "b");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, false,
                "b");
        String str = m.removeFirst();
        assertEquals(mExpected, m);
        assertEquals("a", str);
    }

    @Test
    public final void testRemoveFirstLeavingMoreThanOne() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, false, "a",
                "b", "c", "d");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, false,
                "b", "c", "d");
        String str = m.removeFirst();
        assertEquals(mExpected, m);
        assertEquals("a", str);
    }

    @Test
    public final void testIsInInsertionModeTrue() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true, "a",
                "b", "c");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true,
                "a", "b", "c");
        boolean b = m.isInInsertionMode();
        assertEquals(mExpected, m);
        assertTrue(b);
    }

    @Test
    public final void testIsInInsertionModeFalse() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, false, "a",
                "b", "c");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, false,
                "a", "b", "c");
        boolean b = m.isInInsertionMode();
        assertEquals(mExpected, m);
        assertFalse(b);
    }

    @Test
    public final void testOrder() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true, "a",
                "b");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true,
                "a", "b");
        Comparator<String> order = m.order();
        assertEquals(mExpected, m);
        assertEquals(order, ORDER);
    }

    @Test
    public final void testSizeEmpty() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true);
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true);
        int size = m.size();
        assertEquals(mExpected, m);
        assertEquals(0, size);
    }

    @Test
    public final void testSizeOne() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true, "a");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true,
                "a");
        int size = m.size();
        assertEquals(mExpected, m);
        assertEquals(1, size);
    }

    @Test
    public final void testSizeMoreThanOne() {
        SortingMachine<String> m = this.createFromArgsTest(ORDER, true, "a",
                "b", "c");
        SortingMachine<String> mExpected = this.createFromArgsRef(ORDER, true,
                "a", "b", "c");
        int size = m.size();
        assertEquals(mExpected, m);
        assertEquals(3, size);
    }
}
