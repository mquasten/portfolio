package de.mq.portfolio.user.support;


import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

import de.mq.portfolio.user.User;

public class UsersCSVLineConverterImpl implements Converter<String[], User> {

    @Override
    public final User convert(final String[] cols) {
        Assert.notNull(cols, "Columns is mandatory.");
        Assert.isTrue(cols.length == 2 , "2 Columns must exists.");
        return new UserImpl(cols[0], cols[1]);
    }

}
