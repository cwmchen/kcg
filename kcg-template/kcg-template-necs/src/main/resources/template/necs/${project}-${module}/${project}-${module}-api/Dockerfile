FROM @{dockerfile.registry}/@{dockerfile.serverJre}

COPY @{dockerfile.jarFilePath} /opt/server/@{dockerfile.projectName}.jar

EXPOSE @{dockerfile.port}

ENTRYPOINT java \
           -Djava.security.egd=file:/dev/./urandom \
           ${JAVA_OPTS} \
           -jar /opt/server/@{dockerfile.projectName}.jar