FROM gitpod/workspace-full

USER gitpod

# Install custom tools, runtime, etc. using apt-get
# For example, the command below would install "bastet" - a command line tetris clone:
#
# RUN sudo apt-get -q update && \
#     sudo apt-get install -yq bastet && \
#     sudo rm -rf /var/lib/apt/lists/*
#
# More information: https://www.gitpod.io/docs/config-docker/
RUN sudo apt-get -q update && \
    sed -i 's/sdkman_auto_answer=false/sdkman_auto_answer=true/g' /home/gitpod/.sdkman/etc/config && \
    /bin/bash -c "source /home/gitpod/.sdkman/bin/sdkman-init.sh; sdk install java 17.0.4.1.fx-zulu -n"

ENV JDK_HOME="/home/gitpod/.sdkman/candidates/java/17.0.4.1.fx-zulu"
