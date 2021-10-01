package com.match.making.configuration;

import com.match.making.entity.payload.UserPayload;
import com.match.making.entity.user.User;
import com.match.making.facade.UserFacade;
import com.match.making.factory.Factory;
import com.match.making.factory.skill.TwoLevelsDiffSkillBucketFactory;
import com.match.making.service.Match;
import com.match.making.service.MatchMaker;
import com.match.making.service.MatchRegistrator;
import com.match.making.service.MatchService;
import com.match.making.storage.DefaultUserStorage;
import com.match.making.storage.UserStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class ServiceConfiguration {

    @Bean
    public UserStorage userStorage() {
        return DefaultUserStorage.init();
    }

    @Bean
    public Factory.SkillBucket skillBucketFactory() {
        return new TwoLevelsDiffSkillBucketFactory();
    }

    @Bean
    public Match.Maker matchMaker(final UserStorage userStorage, final Factory.SkillBucket skillBucketFactory) {
        return new MatchMaker(userStorage, skillBucketFactory);
    }

    @Bean
    public Match.Registrator matchRegistrator(final UserStorage userStorage) {
        return new MatchRegistrator(userStorage);
    }

    @Bean
    public Match.Service matchService(final Match.Maker matchMaker, final Match.Registrator matchRegistrator) {
        return new MatchService(matchMaker, matchRegistrator);
    }

    @Bean
    public UserFacade userFacade(final Match.Service matchService, final Converter<UserPayload, User> converter) {
        return new UserFacade(matchService, converter);
    }
}
