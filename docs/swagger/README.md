# ShootPointer Static Swagger

This folder is a static Swagger UI artifact for portfolio publishing.

It does not deploy the Spring Boot backend. It publishes API documentation from `openapi.json`, generated from the editable `openapi.yaml` source.

## Local Preview

Open this file in a browser:

```text
docs/swagger/index.html
```

For an HTTP preview:

```bash
python3 -m http.server 8080 --directory docs/swagger
```

Then open:

```text
http://localhost:8080
```

## GitHub Pages

1. Commit and push this repository.
2. Go to GitHub repository `Settings` > `Pages`.
3. Set `Source` to `GitHub Actions`.
4. Run the `Deploy Swagger Docs` workflow, or push to `main`.
5. Open the generated URL:

```text
https://<github-id>.github.io/<repository-name>/swagger/
```

Do not use `Deploy from a branch` with `/docs` for this repo. That path can trigger GitHub Pages' default Jekyll build, which is unnecessary for this static Swagger UI.

## Vercel

1. Import the repository in Vercel.
2. Set `Root Directory` to `docs/swagger`.
3. Leave build command empty.
4. Set output directory to `.` if Vercel asks.
5. Deploy and open the generated `*.vercel.app` URL.

## Notes

- `Try it out` is disabled because this page does not host a backend server.
- No database, Redis, MongoDB, Elasticsearch, Kakao OAuth credentials, or secrets are required.
- Update `openapi.yaml` when controller paths or request/response DTOs change, then regenerate `openapi.json`.

```bash
ruby -e "require 'yaml'; require 'json'; doc = YAML.load_file('docs/swagger/openapi.yaml'); File.write('docs/swagger/openapi.json', JSON.pretty_generate(doc))"
```
