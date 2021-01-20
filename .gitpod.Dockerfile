FROM gitpod/workspace-postgres

USER gitpod

RUN sudo apt update && \
    sudo rm -rf /var/lib/apt/lists/*