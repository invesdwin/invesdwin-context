package de.invesdwin.metaproject.test;

import java.math.BigInteger;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.junit.Test;

import de.invesdwin.context.JavaServicesTest;
import de.invesdwin.context.integration.Marshallers;
import de.invesdwin.context.test.ATest;
import de.invesdwin.metaproject.test.schema.JavaTypesTestContainer;
import de.invesdwin.metaproject.test.schema.JavaTypesTestPayload;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
public class XjcTest extends ATest {

    @Test
    public void testMarshallingOfAllTypes() throws Exception {
        new JavaServicesTest().testServices();

        final JavaTypesTestContainer o = fillTestObject();
        final String xml = Marshallers.toXml(o);
        log.info("Before:\n" + xml);
        final JavaTypesTestContainer oFromXml = Marshallers.fromXml(xml);
        final String xmlFromOFromXml = Marshallers.toXml(oFromXml);
        log.info("After:\n" + xmlFromOFromXml);
        Assertions.assertThat(xmlFromOFromXml).as("Marshal->Unmarshal->Marshal distortes the result!").isEqualTo(xml);
    }

    //CHECKSTYLE:OFF
    private JavaTypesTestContainer fillTestObject() throws DatatypeConfigurationException {
        //CHECKSTYLE:ON
        final String string = "string";
        final byte[] byteArray = { 0, 1, 2, 3 };
        final Boolean bboolean = Boolean.TRUE;
        final Byte bbyte = 1;
        final FDate date = new FDate();
        final Decimal decimal = new Decimal("1");
        final Double ddouble = 1.0;
        final javax.xml.datatype.Duration duration = DatatypeFactory.newInstance().newDuration(1);
        final Float ffloat = 1f;
        //        Object object = new Object();
        final Integer integer = 1;
        final BigInteger bigInteger = BigInteger.ONE;
        final BigInteger negBigInteger = new BigInteger("-1");
        final Long llong = 1L;
        //        QName qName = new QName("1");
        final Short sshort = 1;

        final JavaTypesTestPayload p = new JavaTypesTestPayload();
        p.setAnySimpleTypeRequired(string);
        p.setAnySimpleTypeOptional(string);
        p.setAnyURIRequired(string);
        p.setAnyURIOptional(string);
        p.setBase64BinaryRequired(byteArray);
        p.setBase64BinaryOptional(byteArray);
        p.setBooleanRequired(bboolean);
        p.setBooleanOptional(bboolean);
        p.setByteRequired(bbyte);
        p.setByteOptional(bbyte);
        p.setDateRequired(date);
        p.setDateOptional(date);
        p.setDateTimeRequired(date);
        p.setDateTimeOptional(date);
        p.setDecimalRequired(decimal);
        p.setDecimalOptional(decimal);
        p.setDoubleRequired(ddouble);
        p.setDoubleOptional(ddouble);
        p.setDurationRequired(duration);
        p.setDurationOptional(duration);
        //        p.setENTITYRequired(string);
        //        p.setENTITYOptional(string);
        p.setFloatRequired(ffloat);
        p.setFloatOptional(ffloat);
        p.setGDayRequired(date);
        p.setGDayOptional(date);
        p.setGMonthRequired(date);
        p.setGMonthOptional(date);
        p.setGMonthDayRequired(date);
        p.setGMonthDayOptional(date);
        p.setGYearRequired(date);
        p.setGYearOptional(date);
        p.setGYearMonthRequired(date);
        p.setGYearMonthOptional(date);
        p.setHexBinaryRequired(byteArray);
        p.setHexBinaryOptional(byteArray);
        //        o.setIDREFRequired(object);
        //        o.setIDREFOptional(object);
        p.setIntRequired(integer);
        p.setIntOptional(integer);
        p.setIntegerRequired(bigInteger);
        p.setIntegerOptional(bigInteger);
        p.setLanguageRequired(string);
        p.setLanguageOptional(string);
        p.setLongRequired(llong);
        p.setLongOptional(llong);
        p.setNameRequired(string);
        p.setNameOptional(string);
        //        p.setNCNameRequired(string);
        //        p.setNCNameOptional(string);
        p.setNegativeIntegerRequired(negBigInteger);
        p.setNegativeIntegerOptional(negBigInteger);
        //        p.setNMTOKENRequired(string);
        //        p.setNMTOKENOptional(string);
        p.setNonNegativeIntegerRequired(bigInteger);
        p.setNonNegativeIntegerOptional(bigInteger);
        p.setNonPositiveIntegerRequired(negBigInteger);
        p.setNonPositiveIntegerOptional(negBigInteger);
        p.setNormalizedStringRequired(string);
        p.setNormalizedStringOptional(string);
        p.setPositiveIntegerRequired(bigInteger);
        p.setPositiveIntegerOptional(bigInteger);
        //        p.setQNameRequired(qName);
        //        p.setQNameOptional(qName);
        p.setShortRequired(sshort);
        p.setShortOptional(sshort);
        p.setStringRequired(string);
        p.setStringOptional(string);
        p.setTimeRequired(date);
        p.setTimeOptional(date);
        //        p.setTokenRequired(string);
        //        p.setTokenOptional(string);
        p.setUnsignedByteRequired(sshort);
        p.setUnsignedByteOptional(sshort);
        p.setUnsignedIntRequired(llong);
        p.setUnsignedIntOptional(llong);
        p.setUnsignedLongRequired(bigInteger);
        p.setUnsignedLongOptional(bigInteger);
        p.setUnsignedShortRequired(integer);
        p.setUnsignedShortOptional(integer);

        final JavaTypesTestContainer t = new JavaTypesTestContainer();
        t.setPayload(p);
        return t;
    }

}
