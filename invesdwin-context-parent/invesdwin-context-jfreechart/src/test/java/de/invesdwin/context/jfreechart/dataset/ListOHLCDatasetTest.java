package de.invesdwin.context.jfreechart.dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.data.general.DatasetGroup;
import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.time.date.FDate;

/**
 * Pins the equals/hashCode contract of ListOHLCDataset: equals declares equality through the series key
 * content, so hashCode is derived from that same field. Content-equal datasets agree on hash and coalesce
 * in hash containers, different keys stay distinguishable, and the hash does not move with the state the
 * inherited AbstractDataset hash used to read (dataset group, notify flag). List operations keep the
 * content-equality semantics of equals.
 */
@NotThreadSafe
public class ListOHLCDatasetTest {

    @Test
    public void testContentEqualDatasetsAgreeOnHash() {
        final ListOHLCDataset one = new ListOHLCDataset("MSFT", newData(1));
        final ListOHLCDataset other = new ListOHLCDataset("MSFT", newData(2));
        Assertions.assertThat(one.equals(other)).isTrue();
        Assertions.assertThat(other.equals(one)).isTrue();
        Assertions.assertThat(one.hashCode()).isEqualTo(other.hashCode());
    }

    @Test
    public void testIdentityPairsStillEqual() {
        final ListOHLCDataset one = new ListOHLCDataset("MSFT", newData(1));
        Assertions.assertThat(one.equals(one)).isTrue();
        Assertions.assertThat(one.hashCode()).isEqualTo(one.hashCode());
        Assertions.assertThat(one.equals(null)).isFalse();
        Assertions.assertThat(one.equals("MSFT")).isFalse();
    }

    @Test
    public void testHashCodeFollowsKeyContent() {
        final ListOHLCDataset msft = new ListOHLCDataset("MSFT", newData(1));
        final ListOHLCDataset ibm = new ListOHLCDataset("IBM", newData(1));
        Assertions.assertThat(msft.equals(ibm)).isFalse();
        Assertions.assertThat(msft.hashCode()).isEqualTo("MSFT".hashCode());
        Assertions.assertThat(ibm.hashCode()).isEqualTo("IBM".hashCode());
    }

    @Test
    public void testEqualPairWithDivergentInheritedStateAgreesOnHash() {
        final ListOHLCDataset one = new ListOHLCDataset("MSFT", newData(1));
        final ListOHLCDataset other = new ListOHLCDataset("MSFT", newData(2));
        //the inherited pair used to read this state, which equals ignores -> equal but different hash
        other.setNotify(false);
        Assertions.assertThat(one.equals(other)).isTrue();
        Assertions.assertThat(one.hashCode()).isEqualTo(other.hashCode());
    }

    @Test
    public void testHashCodeStableAgainstInheritedState() {
        final ListOHLCDataset one = new ListOHLCDataset("MSFT", newData(1));
        final int before = one.hashCode();
        one.setNotify(false);
        Assertions.assertThat(one.hashCode()).isEqualTo(before);
        one.setNotify(true);
        one.setGroup(new DatasetGroup("SOME_OTHER_GROUP"));
        Assertions.assertThat(one.hashCode()).isEqualTo(before);
        Assertions.assertThat(one.hashCode()).isEqualTo("MSFT".hashCode());
    }

    @Test
    public void testHashContainersCoalesceContentEqualDatasets() {
        final Set<ListOHLCDataset> set = ILockCollectionFactory.getInstance(false).newSet();
        set.add(new ListOHLCDataset("MSFT", newData(1)));
        set.add(new ListOHLCDataset("MSFT", newData(2)));
        Assertions.assertThat(set.size()).isEqualTo(1);

        final Map<ListOHLCDataset, String> map = ILockCollectionFactory.getInstance(false).newMap();
        map.put(new ListOHLCDataset("MSFT", newData(1)), "msft");
        Assertions.assertThat(map.get(new ListOHLCDataset("MSFT", newData(9)))).isEqualTo("msft");
        map.put(new ListOHLCDataset("IBM", newData(1)), "ibm");
        Assertions.assertThat(map.size()).isEqualTo(2);
        Assertions.assertThat(map.get(new ListOHLCDataset("IBM", newData(3)))).isEqualTo("ibm");
    }

    @Test
    public void testListOperationsUseContentEquality() {
        final List<ListOHLCDataset> list = new ArrayList<>();
        list.add(new ListOHLCDataset("MSFT", newData(1)));
        Assertions.assertThat(list.contains(new ListOHLCDataset("MSFT", newData(5)))).isTrue();
        Assertions.assertThat(list.indexOf(new ListOHLCDataset("IBM", newData(5)))).isEqualTo(-1);
        Assertions.assertThat(list.remove(new ListOHLCDataset("MSFT", newData(6)))).isTrue();
        Assertions.assertThat(list.isEmpty()).isTrue();
    }

    @Test
    public void testCloneStaysEqualAndHashEqual() throws CloneNotSupportedException {
        final ListOHLCDataset one = new ListOHLCDataset("MSFT", newData(1));
        final ListOHLCDataset clone = (ListOHLCDataset) one.clone();
        Assertions.assertThat(one.getData().equals(clone.getData())).isTrue();
        Assertions.assertThat(clone.equals(one)).isTrue();
        Assertions.assertThat(clone.hashCode()).isEqualTo(one.hashCode());
    }

    private static List<TimeRangedOHLCDataItem> newData(final double value) {
        final List<TimeRangedOHLCDataItem> data = new ArrayList<>();
        data.add(new TimeRangedOHLCDataItem(new FDate(1000L), new FDate(2000L), value, value, value, value, value));
        return data;
    }

}
