FROM python:3.11-slim

WORKDIR /app


COPY . /app/postgres

WORKDIR /app/postgres

RUN pip install --no-cache-dir -r alembic_requirements.txt

# Run all Alembic migrations at container startup
CMD ["alembic", "upgrade", "head"]
