/*
 * Copyright The Original Author or Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.jenkins.plugins.opentelemetry.backend.elastic;

import io.jenkins.plugins.opentelemetry.job.log.LogStorageRetriever;
import io.jenkins.plugins.opentelemetry.job.log.LogsQueryContext;
import io.jenkins.plugins.opentelemetry.job.log.LogsQueryResult;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ElasticsearchRetrieverIT {

    @Test
    public void test() throws IOException {
        InputStream envAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(".env");
        Assert.assertTrue(".env file not found in classpath", envAsStream != null);
        Properties env = new Properties();
        env.load(envAsStream);

        String url = env.getProperty("elasticsearch.url");
        String username = env.getProperty("elasticsearch.username");
        String password = env.getProperty("elasticsearch.password");
        String indexPattern = "logs-apm.app-*";

        LogStorageRetriever elasticsearchLogStorageRetriever = new ElasticsearchLogStorageScrollingRetriever(
            url,
            new UsernamePasswordCredentials(username, password),
            indexPattern);

        int counter = 0;
        LogsQueryContext logsQueryContext = null;
        LogsQueryResult logsQueryResult;
        do {
            //System.out.println("Request " + counter);
            logsQueryResult = elasticsearchLogStorageRetriever.overallLog("ed4e940f4a2f817e9449ed2d3d7248cb", "", logsQueryContext);
            logsQueryContext = logsQueryResult.getLogsQueryContext();
            counter++;
        } while (!logsQueryResult.isComplete());
    }
}