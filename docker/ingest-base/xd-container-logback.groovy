/* Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
import org.springframework.xd.dirt.util.logging.CustomLoggerConverter
import org.springframework.xd.dirt.util.logging.VersionPatternConverter
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy

// We highly recommended that you always add a status listener just
// after the last import statement and before all other statements
// NOTE - this includes logging configuration in the log and stacktraces in the event of errors
// statusListener(OnConsoleStatusListener)

// Emulates Log4j formatting
conversionRule("category", CustomLoggerConverter)

//XD Version
conversionRule("version", VersionPatternConverter)

def ISO8601 = "yyyy-MM-dd'T'HH:mm:ssZ"
def datePattern = ISO8601

appender("STDOUT", ConsoleAppender) {
	encoder(PatternLayoutEncoder) {
		pattern = "%d{${datePattern}} %version %level{5} %thread %category{2} - %msg%n"
	}
}

def logfileNameBase = "${System.getProperty('xd.home')}/logs/container-${System.getProperty('PID')}"

appender("FILE", RollingFileAppender) {
	file = "${logfileNameBase}.log"
	append = true
	rollingPolicy(TimeBasedRollingPolicy) {
		fileNamePattern = "${logfileNameBase}-%d{yyyy-MM-dd}.%i.log"
		timeBasedFileNamingAndTriggeringPolicy(SizeAndTimeBasedFNATP) {
			maxFileSize = "100MB"
		}
		maxHistory = 30
	}

	encoder(PatternLayoutEncoder) {
		pattern = "%d{${datePattern}} %version %level{5} %thread %category{2} - %msg%n"
	}
}

root(WARN, ["STDOUT", "FILE"])

logger("org.nasa", DEBUG)
logger("org.springframework.scheduling.concurrent", DEBUG, ["FILE"], false)

logger("org.springframework.xd", WARN)
logger("org.springframework.xd.dirt.server", INFO)
logger("org.springframework.xd.dirt.util.XdConfigLoggingInitializer", INFO)
logger("xd.sink", INFO)
logger("org.springframework.xd.sqoop", INFO)

logger("org.springframework", WARN)
logger("org.springframework.boot", WARN)
logger("org.springframework.integration", WARN)
logger("org.springframework.retry", WARN)
logger("org.springframework.amqp", WARN)

logger("org.nasa.ingest.tcpshell", INFO)

//This prevents the "Error:KeeperErrorCode = NodeExists" INFO messages
//logged by ZooKeeper when a parent node does not exist while
//invoking Curator's creatingParentsIfNeeded node builder.
logger("org.apache.zookeeper.server.PrepRequestProcessor", WARN)

// This prevents the WARN level about a non-static, @Bean method in Spring Batch that is irrelevant
logger("org.springframework.context.annotation.ConfigurationClassEnhancer", ERROR)

// This prevents boot LoggingApplicationListener logger's misleading warning message
logger("org.springframework.boot.logging.LoggingApplicationListener", ERROR)

// This prevents Hadoop configuration warnings
logger("org.apache.hadoop.conf.Configuration", ERROR)


//This is for the throughput-sampler sink module
logger("org.springframework.xd.integration.throughput", INFO)

// Suppress json-path warning until SI 4.2 is released
logger("org.springframework.integration.config.IntegrationRegistrar", ERROR)
