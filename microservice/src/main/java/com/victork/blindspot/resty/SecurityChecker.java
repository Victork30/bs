package com.victork.blindspot.resty;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

public class SecurityChecker {

    private static final String NPM_REGISTRY_URL = "https://registry.npmjs.org/";
    private static final String GITHUB_API_URL = "https://api.github.com/repos/";

    public void check(String jsonString) {
        //String jsonString = "{\"package1\":\"first-package-name\",\"package2\":\"second-package-name\",\"package3\":\"third-package-name\"}";
        JSONObject json = new JSONObject(jsonString);

        JSONObject resultJson = new JSONObject();

        RestTemplate restTemplate = new RestTemplate();

        for (String key : json.keySet()) {
            String packageName = json.getString(key);
            JSONObject packageInfo = getPackageInfo(packageName, restTemplate);
            JSONObject healthInfo = checkSecurityHealth(packageInfo, restTemplate);
            resultJson.put(packageName, healthInfo);
        }

        System.out.println(resultJson.toString());
    }

    private static JSONObject getPackageInfo(String packageName, RestTemplate restTemplate) {
        String packageUrl = NPM_REGISTRY_URL + packageName;
        String packageInfo = restTemplate.getForObject(packageUrl, String.class);
        System.out.println("Debug getPackageInfo: " + packageInfo);

        return new JSONObject(packageInfo);
    }

    private static JSONObject checkSecurityHealth(JSONObject packageInfo, RestTemplate restTemplate) {
        JSONObject healthInfo = new JSONObject();

        String version = packageInfo.getString("version");
        Date lastPublished = new Date(packageInfo.getLong("time"));
        int maintainers = packageInfo.getJSONArray("maintainers").length();
        String repository = packageInfo.getJSONObject("repository").getString("url");

        healthInfo.put("version", version);

        boolean lastVersionCheck = ChronoUnit.DAYS.between(lastPublished.toInstant(), Instant.now()) <= 30;
        healthInfo.put("lastVersionCheck", lastVersionCheck);

        boolean maintainersCheck = maintainers >= 2;
        healthInfo.put("maintainersCheck", maintainersCheck);

        JSONObject commitInfo = getCommitInfo(repository, restTemplate);
        if (commitInfo != null) {
            Date lastCommitDate = new Date(commitInfo.getLong("commit") * 1000);
            boolean lastCommitCheck = ChronoUnit.DAYS.between(lastCommitDate.toInstant(), Instant.now()) <= 14;
            healthInfo.put("lastCommitCheck", lastCommitCheck);
        } else {
            healthInfo.put("lastCommitCheck", false);
        }

        return healthInfo;
    }
    
    private static JSONObject getCommitInfo(String repository, RestTemplate restTemplate) {
        // Extract the GitHub repository name from the repository URL
        String[] parts = repository.split("/");
        String repoName = parts[parts.length - 1];
        // Construct the GitHub API URL for the repository's commits
        String commitUrl = GITHUB_API_URL + repoName + "/commits";
        try {
            // Make a GET request to the GitHub API to retrieve the latest commit information
            String commitInfo = restTemplate.getForObject(commitUrl, String.class);
            // Parse the response as a JSON object
            JSONArray commitJsonArray = new JSONArray(commitInfo);
            return commitJsonArray.getJSONObject(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
