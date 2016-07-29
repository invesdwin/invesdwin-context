package de.invesdwin.context.beans;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Test;

import com.mysema.query.alias.Alias;
import com.mysema.query.collections.CollQuery;
import com.mysema.query.types.path.ComparablePath;
import com.mysema.query.types.path.EntityPathBase;

import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.bean.AValueObject;

@ThreadSafe
public class QueryDslCollectionsTest extends ATest {

    @Test
    public void testQueryDslWithSimpleClass() {
        final List<CloneableClass> vos = new ArrayList<CloneableClass>();
        for (int i = 0; i < 5; i++) {
            final CloneableClass vo = new CloneableClass();
            vo.setOtherValue(i);
            vos.add(vo);
        }
        final CloneableClass vo = Alias.alias(CloneableClass.class, "vo");
        Assertions.assertThat(vo).isNotNull();
        final CollQuery query = new CollQuery();
        final EntityPathBase<CloneableClass> fromVo = Alias.$(vo);
        Assertions.assertThat(fromVo).isNotNull();
        query.from(fromVo, vos);
        query.where(Alias.$(vo.getOtherValue()).eq(1));
        final List<CloneableClass> result = query.list(Alias.$(vo));

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getOtherValue()).isEqualTo(1);
    }

    @Test
    public void testQueryDslWithComparableValueObject() {
        final List<CloneableVO> vos = new ArrayList<CloneableVO>();
        for (int i = 0; i < 5; i++) {
            final CloneableVO vo = new CloneableVO();
            vo.setValue(i);
            vos.add(vo);
        }
        final CloneableVO vo = Alias.alias(CloneableVO.class, "vo");
        Assertions.assertThat(vo).isNotNull();
        final CollQuery query = new CollQuery();
        final ComparablePath<CloneableVO> fromVo = Alias.$(vo);
        Assertions.assertThat(fromVo).as("https://bugs.launchpad.net/querydsl/+bug/785935").isNotNull();
        query.from(fromVo, vos);
        query.where(Alias.$(vo.getValue()).eq(1));
        final List<CloneableVO> result = query.list(Alias.$(vo));

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getValue()).isEqualTo(1);
    }

    public static class CloneableVO extends AValueObject {
        private static final long serialVersionUID = 1L;

        private Integer value;

        private MutableInt mutableValue;

        public Integer getValue() {
            return value;
        }

        public void setValue(final Integer value) {
            this.value = value;
        }

        public MutableInt getMutableValue() {
            return mutableValue;
        }

        public void setMutableValue(final MutableInt mutableValue) {
            this.mutableValue = mutableValue;
        }

    }

    public static class CloneableClass implements Cloneable {
        private CloneableVO value;
        private Integer otherValue;

        public CloneableVO getValue() {
            return value;
        }

        public void setValue(final CloneableVO value) {
            this.value = value;
        }

        public Integer getOtherValue() {
            return otherValue;
        }

        public void setOtherValue(final Integer otherValue) {
            this.otherValue = otherValue;
        }

        @Override
        public Object clone() {
            try {
                return super.clone();
            } catch (final CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
