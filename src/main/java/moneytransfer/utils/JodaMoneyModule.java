package moneytransfer.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableMap;
import org.joda.money.BigMoney;
import org.joda.money.BigMoneyProvider;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.money.format.MoneyAmountStyle;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;

import java.io.IOException;
import java.math.BigDecimal;


public class JodaMoneyModule extends SimpleModule {

    public JodaMoneyModule() {
        super(JodaMoneyModule.class.getName(), PackageVersion.VERSION);

        addSerializer(BigMoneyProvider.class, new BigMoneyProviderSerializer());
        addDeserializer(Money.class, new MoneyDeserializer());
        addDeserializer(BigMoney.class, new BigMoneyDeserializer());

        addSerializer(CurrencyUnit.class, new CurrencyUnitSerializer());
        addDeserializer(CurrencyUnit.class, new CurrencyUnitDeserializer());
    }

    public static class BigMoneyProviderSerializer extends JsonSerializer<BigMoneyProvider> {
        private static final MoneyFormatter amountFormatter = new MoneyFormatterBuilder().appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_NO_GROUPING).toFormatter();
        private static final MoneyFormatter currencyFormatter = new MoneyFormatterBuilder().appendCurrencyCode().toFormatter();

        @Override
        public void serialize(BigMoneyProvider value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (value == null) return;
            jsonGenerator.writeObject(ImmutableMap.of(
                    "currency", currencyFormatter.print(value),
                    "value", amountFormatter.print(value)
            ));
        }
    }

    public static class MoneyDeserializer extends JsonDeserializer<Money> {
        @Override
        public Money deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
            JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
            return Money.of(
                    CurrencyUnit.of(jsonNode.get("currency").asText()),
                    new BigDecimal(jsonNode.get("value").asText())
            );
        }
    }

    public static class BigMoneyDeserializer extends JsonDeserializer<BigMoney> {
        private static final MoneyDeserializer moneyDeserializer = new MoneyDeserializer();

        @Override
        public BigMoney deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
            return moneyDeserializer.deserialize(jsonParser, context).toBigMoney();
        }
    }

    public static class CurrencyUnitSerializer extends JsonSerializer<CurrencyUnit> {
        @Override
        public void serialize(CurrencyUnit value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
            if (value == null) return;
            jsonGenerator.writeString(value.getCurrencyCode());
        }
    }

    public static class CurrencyUnitDeserializer extends JsonDeserializer<CurrencyUnit> {
        @Override
        public CurrencyUnit deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
            return CurrencyUnit.of(jsonParser.getText());
        }
    }
}
