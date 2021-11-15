## Usage

VAULT tokens will typically expire. You could have Travis obtain a token for each run by calling the vault API. You’ll still need a credential that you can use to authenticate against VAULT. 

## The Logic 

If you put a VAULT token in your `env vars`  it would need human intervention at or near the TTL. I’m not sure how others are doing it, but you could use the userpass secret engine and store those encrypted variables in Travis.

Then as part of your build, use those to get a short lived VAULT token with the capabilities that your app needs, you can "VAULT Hop" in a sense. 

So perhaps a static encrypted VAULT username/password dedicated to Travis => `/v2/auth/userpass/login/` which would return a `auth.client_token`.

## Why use VAULT? 

HashiCorp VAULT adds an extra layer of security to your tokens, secrets - and lets you manage them. I recommend using VAULT anytime you can. This is just a cursory example of something bigger I'm working on for Travis CI, currently I have HashiCorp Terraform completed (integration), and very close as you can see here getting VAULT integreated. 

## Integration 

```yaml
env:
  - VAULT_ADDR=http://localhost:8200
sudo: required
before_install:
  - wget https://releases.hashicorp.com/vault/0.5.2/vault_0.5.2_linux_amd64.zip
  - unzip vault_0.5.2_linux_amd64.zip
  - ./vault server -dev &
before_script:
  - ./vault auth-enable userpass
  - ./vault write auth/userpass/users/montana password=foo policies=root
  - ./vault auth-enable app-id
  - ./vault write auth/app-id/map/app-id/foo value=root display_name=foo
  - ./vault write auth/app-id/map/user-id/bar value=foo
script: mvn clean test
````
You can see we fetch VAULT, then start writing auth/userpass that's read `foo` `bar`. We then run a Maven test. This Travis CI build is now using VAULT.
