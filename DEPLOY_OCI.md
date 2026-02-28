# Deploy On OCI (24/7) - This Project

## 1) On your OCI VM (Ubuntu), install Docker

```bash
sudo apt update
sudo apt install -y docker.io docker-compose-v2 git
sudo usermod -aG docker $USER
newgrp docker
```

## 2) Open OCI and VM firewall ports

- OCI Security List / NSG: open inbound `80`, `443`, `8080` (and `22` for SSH).
- VM firewall (if enabled):

```bash
sudo ufw allow 22
sudo ufw allow 80
sudo ufw allow 443
sudo ufw allow 8080
sudo ufw enable
```

## 3) Get your code on the VM

```bash
git clone <your-repo-url> forum
cd forum
```

## 4) Update DB passwords in compose

Edit `docker-compose.oci.yml` and replace:

- `forum_pass_change_me`
- `root_pass_change_me`

## 5) Start app + MySQL

```bash
docker compose -f docker-compose.oci.yml up -d --build
docker compose -f docker-compose.oci.yml ps
```

Open:

- `http://<OCI_PUBLIC_IP>:8080/forum`

## 6) Update/redeploy

```bash
git pull
docker compose -f docker-compose.oci.yml up -d --build
```

## Notes

- DB data is persisted in Docker volume `db_data`.
- `sql/init.sql` runs automatically only on first DB creation.
- App uses env vars (`DB_URL`, `DB_USER`, `DB_PASSWORD`) via `AppContextListener`.
- This repo deploys using `build/classes`. Before pushing code, make sure Eclipse has rebuilt the project so updated `.class` files are present.
