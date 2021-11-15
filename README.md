## Usage

VAULT tokens will typically expire. You could have Travis obtain a token for each run by calling the VAULT API. You’ll still need a credential that you can use to authenticate against VAULT. 

## The Logic 

If you put a VAULT token in your `env vars`  it would need human intervention at or near the TTL. I’m not sure how others are doing it, but you could use the userpass secret engine and store those encrypted variables in Travis.

Then as part of your build, use those to get a short lived VAULT token with the capabilities that your app needs, you can "VAULT Hop" in a sense. 

So perhaps a static encrypted VAULT username/password dedicated to Travis => `/v2/auth/userpass/login/` which would return a `auth.client_token`.

![image](https://user-images.githubusercontent.com/20936398/141844521-16bf41d6-312d-4f26-a97f-8e0678901b6c.png)


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

<img width="900" alt="Screen Shot 2021-11-15 at 11 44 47 AM" src="https://user-images.githubusercontent.com/20936398/141844213-367ed9a8-47fe-4457-a3de-c60e4b241371.png">

## Role ID's 

RoleID is an identifier that selects the AppRole against which the other credentials are evaluated. When authenticating against this auth method's login endpoint, the RoleID is a required argument (via role_id ) at all times.

![image](https://user-images.githubusercontent.com/20936398/141844618-c5cc712a-13be-4bb0-9abb-db73d1b0f6ab.png)


## Authenticaion 

Via the CLI: 

```bash
curl \
    --request POST \
    --data '{"role_id":"988a9df-...","secret_id":"37b74931..."}' \
    http://127.0.0.1:8200/v1/auth/approle/login
 ```

## SecretID

SecretID is a credential that is required by default for any login (via `secret_id`) and is intended to always be secret. (For advanced usage, requiring a SecretID can be disabled via an AppRole's `bind_secret_id` parameter, allowing machines with only knowledge of the RoleID, or matching other set constraints, to fetch a token). SecretIDs can be created against an AppRole either via generation of a 128-bit purely random UUID by the role itself (Pull mode) or via specific, custom values (Push mode). Similarly to tokens, SecretIDs have properties like usage-limit, TTLs and expirations.
