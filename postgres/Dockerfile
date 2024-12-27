FROM python:3.11-slim

COPY . /app/database

WORKDIR /app/database

RUN apt-get update && apt-get install -y \
    build-essential \
    libpq-dev \
    default-libmysqlclient-dev \
    && rm -rf /var/lib/apt/lists/*

RUN pip install --no-cache-dir -r alembic_requirements.txt

# Run all Alembic migrations at container startup
CMD ["alembic", "upgrade", "head"]
