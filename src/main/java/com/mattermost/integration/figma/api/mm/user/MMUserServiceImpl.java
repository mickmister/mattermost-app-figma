package com.mattermost.integration.figma.api.mm.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mattermost.integration.figma.input.mm.user.MMChannelUser;
import com.mattermost.integration.figma.input.mm.user.MMUser;
import com.mattermost.integration.figma.utils.json.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class MMUserServiceImpl implements MMUserService {
    private static final String USERS_GET_URL = "/api/v4/users/ids";
    private static final String USERS_GET_URL_BY_CHANNEL_ID = "%s/api/v4/channels/%s/members";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JsonUtils jsonUtils;

    @Override
    public List<MMUser> getUsersById(List<String> ids, String mattermostSiteUrl, String token) {
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", String.format("Bearer %s", token));
        HttpEntity<Object> request = new HttpEntity<>(ids, headers);
        ResponseEntity<String> usersResponse = restTemplate.postForEntity(mattermostSiteUrl.concat(USERS_GET_URL),
                request, String.class);
        return (List<MMUser>) jsonUtils.convertStringToObject(usersResponse.getBody(),
                new TypeReference<List<MMUser>>() {
                }).get();
    }

    @Override
    public MMUser getUserById(String id, String mattermostSiteUrl, String token) {
        return getUsersById(Collections.singletonList(id), mattermostSiteUrl, token).get(0);
    }

    @Override
    public List<MMChannelUser> getUsersByChannelId(String channelId, String mattermostSiteUrl, String token) {
        String url = String.format(USERS_GET_URL_BY_CHANNEL_ID, mattermostSiteUrl, channelId);
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", String.format("Bearer %s", token));
        HttpEntity<Object> request = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<List<MMChannelUser>>() {
        }).getBody();
    }
}
