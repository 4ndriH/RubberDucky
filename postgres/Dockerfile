FROM python:3.11-slim

# Debug: Print current directory before COPY
RUN echo "Current directory before COPY:" && pwd && echo "Contents:" && ls -la

COPY . /app/database

# Debug: Print destination directory after COPY
RUN echo "Current directory after COPY:" && pwd && echo "Contents of /app/database:" && ls -la /app/database

WORKDIR /app/database

RUN apt-get update && apt-get install -y \
    build-essential \
    libpq-dev \
    default-libmysqlclient-dev \
    && rm -rf /var/lib/apt/lists/*

RUN pip install --no-cache-dir -r alembic_requirements.txt

# Run all Alembic migrations at container startup
CMD ["alembic", "upgrade", "head"]
