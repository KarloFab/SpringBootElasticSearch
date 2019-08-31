package com.karlo.elasticsearch.dao;

import com.karlo.elasticsearch.domain.User;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

@Repository
public class UserDAOImpl implements UserDAO {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${elasticsearch.index.name}")
    private String indexName;

    @Value("${elasticsearch.user.type}")
    private String userTypeName;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public List<User> findAllUsers() {
        SearchQuery findAllQuery = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery()).build();
        return elasticsearchTemplate.queryForList(findAllQuery, User.class);
    }

    @Override
    public User findById(String userId) {
        SearchQuery findByIdQuery = new NativeSearchQueryBuilder()
                .withFilter(matchQuery("userId", userId)).build();

        List<User> users = elasticsearchTemplate.queryForList(findByIdQuery, User.class);
        if (!users.isEmpty()) {
            return users.get(0);
        }

        return null;
    }

    @Override
    public User save(User user) {
        IndexQuery userQuery = new IndexQuery();
        userQuery.setIndexName(indexName);
        userQuery.setObject(user);
        userQuery.setType(userTypeName);

        log.info("User indexed: {}", elasticsearchTemplate.index(userQuery));
        elasticsearchTemplate.refresh(indexName);

        return user;
    }

    @Override
    public Object findAllUserSettings(String userId) {
        SearchQuery findAllUsersSettingsQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("userId", userId)).build();

        List<User> users = elasticsearchTemplate.queryForList(findAllUsersSettingsQuery, User.class);
        if (!users.isEmpty()) {
            return users.get(0).getUserSettings();
        }

        return null;
    }

    @Override
    public String findUserSettings(String userId, String key) {
        SearchQuery findUserSettingsQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("userId", userId)).build();

        List<User> users = elasticsearchTemplate.queryForList(findUserSettingsQuery, User.class);
        if (!users.isEmpty()) {
            return users.get(0).getUserSettings().get(key);
        }

        return null;
    }

    @Override
    public String saveUserSettings(String userId, String key, String value) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("userId", userId)).build();

        List<User> users = elasticsearchTemplate.queryForList(searchQuery, User.class);
        if (!users.isEmpty()) {

            User user = users.get(0);
            user.getUserSettings().put(key, value);

            IndexQuery userQuery = new IndexQuery();
            userQuery.setIndexName(indexName);
            userQuery.setType(userTypeName);
            userQuery.setId(user.getUserId());
            userQuery.setObject(user);
            elasticsearchTemplate.index(userQuery);

            return "Setting added.";
        }

        return null;
    }
}
