/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.opencga.server;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opencb.opencga.core.common.Config;
import org.opencb.opencga.core.common.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


@Path("/util")
public class UtilsWSServer extends OpenCGAWSServer {

    public UtilsWSServer(@PathParam("version") String version, @Context UriInfo uriInfo, @Context HttpServletRequest httpServletRequest) throws IOException {
        super(version, uriInfo, httpServletRequest);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/network/community")
    public Response community(@FormDataParam("sif") String sifData,
                              @DefaultValue("F") @FormDataParam("directed") String directed,
                              @DefaultValue("F") @FormDataParam("weighted") String weighted,
                              @DefaultValue("infomap") @FormDataParam("method") String method) throws IOException {

        String home = Config.getGcsaHome();
        Properties analysisProperties = Config.getAnalysisProperties();
        Properties accountProperties = Config.getAccountProperties();

        String scriptName = "communities-structure-detection";
        java.nio.file.Path scriptPath = Paths.get(home, analysisProperties.getProperty("OPENCGA.ANALYSIS.BINARIES.PATH"), scriptName, scriptName + ".r");

        // creating a random tmp folder
        String rndStr = StringUtils.randomString(20);
        java.nio.file.Path randomFolder = Paths.get(accountProperties.getProperty("OPENCGA.TMP.PATH"), rndStr);
        Files.createDirectory(randomFolder);

        java.nio.file.Path inFilePath = randomFolder.resolve("file.sif");
        java.nio.file.Path outFilePath = randomFolder.resolve("result.comm");
        java.nio.file.Path outFilePath2 = randomFolder.resolve("result.json");

        Files.write(inFilePath, sifData.getBytes(), StandardOpenOption.CREATE_NEW);

        String command = "Rscript " + scriptPath.toString() + " " + method + " " + directed + " " + weighted + " " + inFilePath.toString() + " " + randomFolder.toString() + "/";

        logger.info(command);
//        DBObjectMap result = new DBObjectMap();
        Map<String, Object> result = new HashMap<>();
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            int exitValue = process.exitValue();

            BufferedReader br = Files.newBufferedReader(outFilePath, Charset.defaultCharset());
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            br.close();
            result.put("attributes", sb.toString());

            br = Files.newBufferedReader(outFilePath2, Charset.defaultCharset());
            ObjectMapper mapper = new ObjectMapper();
            JsonFactory factory = mapper.getFactory();
            JsonParser jp = factory.createParser(br);
            JsonNode jsonNode = mapper.readTree(jp);
            result.put("results", jsonNode);

            br.close();

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            result.put("error", "could not read result files");
        }

        return createOkResponse(result);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/network/topological-study")
    public Response topology(@FormDataParam("sif") String sifData,
                             @DefaultValue("F") @FormDataParam("directed") String directed,
                             @DefaultValue("F") @FormDataParam("weighted") String weighted) throws IOException {

        String home = Config.getGcsaHome();
        Properties analysisProperties = Config.getAnalysisProperties();
        Properties accountProperties = Config.getAccountProperties();

        String scriptName = "topological-study";
        java.nio.file.Path scriptPath = Paths.get(home, analysisProperties.getProperty("OPENCGA.ANALYSIS.BINARIES.PATH"), scriptName, scriptName + ".r");

        // creating a random tmp folder
        String rndStr = StringUtils.randomString(20);
        java.nio.file.Path randomFolder = Paths.get(accountProperties.getProperty("OPENCGA.TMP.PATH"), rndStr);
        Files.createDirectory(randomFolder);

        java.nio.file.Path inFilePath = randomFolder.resolve("file.sif");
        java.nio.file.Path outFilePath = randomFolder.resolve("result.local");
        java.nio.file.Path outFilePath2 = randomFolder.resolve("result.global.json");

        Files.write(inFilePath, sifData.getBytes(), StandardOpenOption.CREATE_NEW);

        String command = "Rscript " + scriptPath.toString() + " " + directed + " " + weighted + " " + inFilePath.toString() + " " + randomFolder.toString() + "/";

        logger.info(command);
//        DBObjectMap result = new DBObjectMap();
        Map<String, Object> result = new HashMap<>();
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            int exitValue = process.exitValue();

            BufferedReader br = Files.newBufferedReader(outFilePath, Charset.defaultCharset());
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            br.close();
            result.put("local", sb.toString());

            br = Files.newBufferedReader(outFilePath2, Charset.defaultCharset());
            ObjectMapper mapper = new ObjectMapper();
            JsonFactory factory = mapper.getFactory();
            JsonParser jp = factory.createParser(br);
            JsonNode jsonNode = mapper.readTree(jp);
            result.put("global", jsonNode);

            br.close();

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            result.put("error", "could not read result files");
        }

        return createOkResponse(result);
    }


    @GET
    @Path("/proxy")
    public Response proxy(@QueryParam("url") String url) {
        System.out.println("url = " + url);
        Response response = ClientBuilder.newClient().target(url).request().header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8").get();
        System.out.println("mediatype = " + response.getMediaType());
        System.out.println("content-type = " + response.getHeaderString("Content-Type"));
        return this.buildResponse(Response.ok(response.readEntity(String.class), response.getMediaType()));
    }

}
