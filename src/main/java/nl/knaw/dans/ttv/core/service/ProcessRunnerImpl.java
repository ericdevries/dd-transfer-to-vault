/*
 * Copyright (C) 2021 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.ttv.core.service;

import nl.knaw.dans.ttv.core.domain.ProcessResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class ProcessRunnerImpl implements ProcessRunner {
    private static final Logger log = LoggerFactory.getLogger(ProcessRunnerImpl.class);

    @Override
    public ProcessResult run(String[] command) throws IOException, InterruptedException {
        var processBuilder = new ProcessBuilder(command);
        return runWithProcessBuilder(command, processBuilder);
    }

    @Override
    public ProcessResult run(String[] command, Path workingDirectory) throws IOException, InterruptedException {
        var processBuilder = new ProcessBuilder(command).directory(workingDirectory.toFile());
        return runWithProcessBuilder(command, processBuilder);
    }

    ProcessResult runWithProcessBuilder(String[] command, ProcessBuilder processBuilder) throws IOException, InterruptedException {
        processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);
        processBuilder.redirectErrorStream(true);
        log.trace("Waiting for command {}", String.join(" ", command));

        try {
            var process = processBuilder.start();
            var output = consumeProcessOutput(process);
            var resultCode = process.waitFor();

            log.trace("Command output for '{}' (return code {}):\n{}", String.join(" ", command), resultCode, output);

            return new ProcessResult(resultCode, output);

        }
        catch (IOException | InterruptedException e) {
            log.error("unable to execute command '{}'", command, e);
            throw e;
        }
    }

    String consumeProcessOutput(Process process) throws IOException {
        var output = new BufferedReader(new InputStreamReader(process.getInputStream()));
        var outputString = new StringBuilder();

        String line;
        while ((line = output.readLine()) != null) {
            log.trace("Process output: {}", line);
            outputString.append(line);
            outputString.append("\n");
        }

        return outputString.toString();
    }
}
