![1_he0fhM1Nfn9XfiXPv8o1Vw copy](https://user-images.githubusercontent.com/20936398/141865622-f25c5cda-3bb6-40d4-856f-4d03aa66197a.png)


# Using VAULT to manage secrets in Travis CI (Full release scheduled January 2022) by Montana Mendy

[![Build Status](https://app.travis-ci.com/Montana/travis-hashicorp-vault-integration.svg?branch=master)](https://app.travis-ci.com/Montana/travis-hashicorp-vault-integration)

# Why manage secrets?

* Increasing number of applications accessing sensitive data:

>> Means secrets distributed over a wider landscape
>> Means increased exposure to threats

* Classic secret management (i.e. encrypted file on a share) will not scale

>>Application access to secrets complicated.
>>Rotation and invalidation of secrets difficult and slow process.
>>>Especially critical when something is compromised!

## Fetching using the Travis API 

You'll want to use the Travis API, to fethch this, so first from the CLI, you'll need to generate a token:

```bash
travis login
travis token
```
Our latest API is `v3`, so let's try and use that one using the Travis API Explorer. Include the token in the Authorization header of each request to https://api.travis-ci.com:

```bash
curl -H "Travis-API-Version: 3" \
     -H "Authorization: token xxxxxxxxxxxx" \
     https://api.travis-ci.com/repos
 ```
 In the near future there will be a `vault:` hook in the `.travis.yml` after I get done doing more debugging and working out the kinks - so for example it could look like this sample `.travis.yml` I just created:
 
 ```yaml
---
language: go
go: 1.13.x
vault: true
virt: edge
branches:
  only:
  - master
  - "/^v\\d+\\.\\d+(\\.\\d+)?(-\\S*)?$/"
install:
- go mod download
- go mod verify
deploy:
- provider: script
  skip_cleanup: true
  script: curl -sL https://git.io/goreleaser | bash
  'true':
    tags: true
    condition: "$TRAVIS_OS_NAME = linux"
global_env:
- CGO_ENABLED=0
- GO111MODULE=on
os: linux
group: stable
dist: focal
 ```
*Environment Variables:*

|GET  | /repo/{provider}/{repository.id}/env_vars       | Template Variable | Type    |
|------|-------------------------------------------------|-------------------|---------|
| GET  | /repo/ {provider} / {repository.slug} /env_vars | Provider          | Unknown |
| GET  | /travis/repo/{repository.id}/env_vars            | repository.Id     | Integer |
| GET  | /repo/ {repository.slug} /env_vars              | repository.slug   | String  |
| POST | /repo/{provider}/{repository.id}/env_vars       | provider          | Missing |

The table I created above is also true for branching. Given the above table I made, you can surmise you can run the following and it will reate an environment variable for an individual repository. It is possible to use the repository id or slug in the request. Remember use namespaced params in the request body to pass the new environment variables:

```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -H "Travis-API-Version: 3" \
  -H "Authorization: token xxxxxxxxxxxx" \
  -d '{ "env_var.name": "FOO", "env_var.value": "bar", "env_var.public": false }' \
  https://api.travis-ci.com/repo/1234/env_vars
```
The same is true for branching:

```bash
env_var.branch
```
The description in JSON should be: 
```json
{
  "the env_var's branch.": {}
}
```
 
## Public methods

<img width="888" alt="Screen Shot 2021-11-15 at 3 44 54 PM" src="https://user-images.githubusercontent.com/20936398/141870213-abdfb7ec-e980-4d74-8aff-d5593404dc48.png">

## VAULT Principles 

<img width="983" alt="Screen Shot 2021-11-15 at 3 10 34 PM" src="https://user-images.githubusercontent.com/20936398/141867158-93fe1ee7-11d1-45c2-87c1-a7cf1045bc83.png">

## Secret management challenges 

* Secret sprawl.
* Secrets rotation.
* `X.509` certs, SSH and cloud access.
* Encryption.
* Multi-platform and multi-cloud.
* Central control and management. 
* Auditing.
* Enforcing compliance and hardware security module.
* Costs, scalability and productivity.

## Things to remember when dealing with variant secrets 

* Accessing Secrets is `/always/` done via HTTP API.
* CLI VAULT client.
* cURL POST/GET.
* Python/Ruby/Go/... libraries.
* Authentication by attribute (IP), token, etc.

## Environment variables

>>The creation of a client is affected by a number of environment variables, following the main VAULT command line client.

* `VAULT_ADDR`: The url of the VAULT server. Must include a protocol (most likely htpps:// but in testing http:// might be used).
* `VAULT_CAPATH`: The path to the CA certificates.
* `VAULT_TOKEN`: A vault token to use in authentication. Only used for token-based authentication. 
* `VAULT_AUTH_GITHUB_TOKEN`: As for the command line client, a GitHub token for authentication using the GitHub authentication backend. 
* `VAULTR_AUTH_METHOD`: The method to use for authentication.

## What's the key things you need to do? 

* Safe storage at rest.
* Secure communication.
* Robust authentication.
* Flexible role-based authorization.
* Easy rotation of secrets.

## Other possible integrations I might work on other than HashiCorp's VAULT

* Chef Vault: k/v secret store.
* Git-Crypt: git encryption.
* Blackbox: k/v secret store.
* Keywhiz: k/v secret store.
* Confidant: IAM management platform.
* Lemur: PKI management platform.

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

![image](https://user-images.githubusercontent.com/20936398/141845139-ac77c32f-3f8e-4cd3-8559-0b50b9081ca2.png)

## Response Wrapping 

When a response is wrapped, the normal API response from Vault does not contain the original secret, but rather contains a set of information related to the response-wrapping token:

* TTL: The TTL of the response-wrapping token itself
* Token: The actual token value
* Creation Time: The time that the response-wrapping token was created
* Creation Path: The API path that was called in the original request
* Wrapped Accessor: If the wrapped response is an authentication response containing a Vault token, this is the value of the wrapped token's accessor. This is useful for orchestration systems (such as Nomad) to be able to control the lifetime of secrets based on their knowledge of the lifetime of jobs, without having to actually unwrap the response-wrapping token or gain knowledge of the token ID inside.

![image](https://user-images.githubusercontent.com/20936398/141845365-4684417f-f909-4431-b00e-9b0889ed7df3.png)

## Libsodium Sealed Boxes

The format of a sealed box is, `ephemeral_pk ‖ box(m, recipient_pk, ephemeral_sk, nonce=blake2b(ephemeral_pk ‖ recipient_pk))`. Sealed boxes are designed to anonymously send messages to a recipient given its public key. Only the recipient can decrypt these messages, using its private key. While the recipient can verify the integrity of the message, it cannot verify the identity of the sender.

A message is encrypted using an ephemeral key pair, whose secret part is destroyed right after the encryption process. Without knowing the secret key used for a given message, the sender cannot decrypt its own message later. And without additional data, a message cannot be correlated with the identity of its sender.

## Libsodium & VAULT

```c
int sodium_hex2bin(unsigned char * const bin, const size_t bin_maxlen,                   const char * const hex, const size_t hex_len,                   const char * const ignore, size_t * const bin_len,                   const char ** const hex_end);
```

## Authenticated encryption with Libsodium & VAULT using "Sealed Boxes"

Below is myself using `Libsodium, VAULT and Travis, for a full on integration: 

```c
#define MESSAGE ((const unsigned char *) "test")
#define MESSAGE_LEN 4
#define CIPHERTEXT_LEN (crypto_secretbox_MACBYTES + MESSAGE_LEN)
​
unsigned char key[crypto_secretbox_KEYBYTES];
unsigned char nonce[crypto_secretbox_NONCEBYTES];
unsigned char ciphertext[CIPHERTEXT_LEN];
​
crypto_secretbox_keygen(key);
randombytes_buf(nonce, sizeof nonce);
crypto_secretbox_easy(ciphertext, MESSAGE, MESSAGE_LEN, nonce, key);
​
unsigned char decrypted[MESSAGE_LEN];
if (crypto_secretbox_open_easy(decrypted, ciphertext, CIPHERTEXT_LEN, nonce, key) != 0) {
    /* message forged! */
}
```
This operation encrypts a message with a key and a nonce to keep it confidential, then computes an authentication tag. This tag is used to make sure that the message hasn't been tampered with before decrypting it.

## Scrypt

`Scrypt` was also designed to make it costly to perform large-scale custom hardware attacks by requiring large amounts of memory. Even though its memory hardness can be significantly reduced at the cost of extra computations, this function remains an excellent choice today, provided that its parameters are properly chosen.

## Libsodium Multi Arch Cores

Libsodium is a shared library with a machine-independent set of headers, so that it can easily be used by 3rd party projects. The library is built using autotools, making it easy to package. Installation is trivial, and both compilation and testing can take advantage of multiple CPU cores. Download a tarball of libsodium, preferably the latest stable version, then follow the ritual:

```bash
./configuremake && make checksudo make install
```

Since different files are compiled for different CPU classes, and to prevent unwanted optimizations, link-time optimization (LTO) should not be used.
Also do not enable sanitizers (such as `-fsanitize=signed-integer-overflow`). These can introduce side channels.

## Libsodium Helpers 

The `sodium_stackzero()` function clears `len` bytes above the current stack pointer, to overwrite sensitive values that may have been temporarily stored on the stack. _Note that these values can still be present in registers._ This function was introduced in `libsodium 1.0.16.`

## Libhydrogen + Libsodium 

Libhydrogen unlike Libsodium is small and easy to audit. Implemented as one tiny file for every set of operation, and adding a single `.c` file to your project is all it takes to use `libhydrogen` in your project. The whole code is released under a single, very liberal license (ISC).

Zero dynamic memory allocations and low stack requirements (median: `32 bytes`, max: `128 bytes`). This makes it usable in constrained environments such as microcontrollers.

Portable: written in standard `C99`. Supports Linux, BSD, MacOS, Windows, and the Arduino IDE out of the box. A msg_id doesn't have to be secret and it doesn't have to be sequential either. 

Some applications might prefer a coarse timestamp instead. Any value up to `2^64-1` is acceptable.

If this mechanism is not required by an application, using a constant `msg_id` such as `0` is also totally fine. Message identifiers are optional and do not have to be unique.

```c
int hydro_secretbox_decrypt(void *m_, const uint8_t *c, size_t clen,
    uint64_t msg_id, const char ctx[hydro_secretbox_CONTEXTBYTES],
    const uint8_t key[hydro_secretbox_KEYBYTES])
    __attribute__((warn_unused_result));
```

The `hydro_secretbox_decrypt()` function decrypts the ciphertext c of length clen (which includes the `hydro_secretbox_HEADERBYTES` bytes header) using the secret key key, the context ctx and the message identifier `msg_id`:

## Probes

If the authentication tag can be verified using these parameters, the function stores the decrypted message into say something like `m_ontana`. The length of this decrypted message is `clen - hydro_secretbox_HEADERBYTES`. It then returns `0.`

If the authentication tag doesn't appear to be valid for these parameters, the function returns `-1.`

Probes can help mitigate this. A probe is a 128-bit value computed from the MAC and a secret key, that can be quickly verified before decrypting the actual ciphertext if the probe happens to pass verification. The key can be the same as the one used for encryption. 

* The sender sends the probe, along with the `ciphertext`.
* The recipient reads the probe, verifies it. If it doesn't pass, the ciphertext can be ignored.
* If it does pass in Travis, the recipient can then decrypt the ciphertext, whose MAC will still be verified.

## Consul templates

Consul Template queries a Consul instance and updates any number of specified templates on the filesystem. As an added bonus, Consul Template can execute arbitrary commands when a template update completes. Consul daemon runs and updates Consul templates as information changes VAULT is a "first class" client for Consul and builds right into template, here's an example below: 

<img width="1052" alt="Screen Shot 2021-11-15 at 2 46 32 PM" src="https://user-images.githubusercontent.com/20936398/141864713-11e4e9ec-94aa-46c4-8ac0-d00848d2bf57.png">

## Ephemeral Secrets

* Secrets that exist for a limited amount of time.
* Secrets dynamically generated on a per-token/application/user basis.
* Lease time allows for secrets/access to be retired.
* Provide secrets on a "need to use" basis.

<img width="641" alt="Screen Shot 2021-11-15 at 2 39 55 PM" src="https://user-images.githubusercontent.com/20936398/141865891-36eda569-cbc1-4258-a1ba-1e581b73e3ef.png">

## Leases

* Secrets from Vault come with a lifetime-requires renewal based on a policy.
* Enforces check-ins (when configured).

![image](https://user-images.githubusercontent.com/20936398/141866005-c051709c-36d6-49c3-b474-2810d0440c6d.png)

## Example PKI as it relates to Travis 

The PKI secret backend for Vault generates X.509 certificates dynamically based on configured roles. This means services can get certificates needed for both client and server authentication without going through the usual manual process of generating a private key and CSR, submitting to a CA, and waiting for a verification and signing process to complete.

Create SSL certificates on the fly- since they're automatically generated, use short TTL/lease and get a new one every week:

```bash
vault write pki/root/generate/internal common_name=myvault.com ttl=87600h
Key             Value
certificate     -----BEGIN CERTIFICATE-----
MIIDvTCCAqWgAwIBAgIUAsza+fvOw+Xh9ifYQ0gNN0ruuWcwDQYJKoZIhvcNAQEL
BQAwFjEUMBIGA1UEAxMLbXl2YXVsdC5jb20wHhcNMTUxMTE5MTYwNDU5WhcNMjUxdfsfsdfds
```

## Ephemeral Leases 

Policy based renewals done through VAULT that will directly affect your Travis builds, this is of course to add more security. I'll attach a code snippet I made below: 

![carbon (2)](https://user-images.githubusercontent.com/20936398/141866603-348241c9-e575-445d-9665-1ac8778499fd.png)

## Scrypt Algorithm

Keep in mind, the Scrypt Algorithm, these are particular notrious for leaking secrets on occasion: 

```c
Function ROMix(Block, Iterations)

Create Iterations copies of X
X ← Block
for i ← 0 to Iterations−1 do
Vi ← X
X ← BlockMix(X)

for i ← 0 to Iterations−1 do
//Convert first 8-bytes of the last 64-byte block of X to a UInt64, assuming little endian (Intel) format
j ← Integerify(X) mod N 
X ← BlockMix(X xor Vj)

return X
```

## TTL and Lease

* Each authentication is attached to a token and it will be used for any subsequent requests. The token is configured with a TTL. 
* The token can be revoked any time if needed or if it is compromised. 
* Dynamic secrets are attached to a lease that can be configured by roles. When the lease expires, the secret automatically expires. 

## Cryptography princples 

* Vault's primary interface is through a HTTP Restful API. Both the CLI and the Web GUI interface with Vault through the same API. A developer would use this API for programmatic access. There is no other way to expose functionality in Vault other than through this API.
* In order to consume secrets, clients (either users or applications) need to establish their identity. While Vault supports the common authentication platforms for users, such as LDAP or Active Directory, it also adds different methods of programatically establishing an application identity based on platform trust, leveraging * AWS IAM, Google IAM, Azure Application Groups, TLS Certificates, and Kubernetes namespaces among others. Upon authentication, and based on certain identity attributes like group membership or project name, Vault will grant a short lived token aligned with a specific set of policies.
* Policies in Vault are decoupled from identities and define what set of secrets a particular identity can access. They are defined as code in HashiCorp Configuration Language (HCL). Rich policy can be defined using Sentinel rules, that are designed to answer "under what condition" an entity would get access to the secret, rather than the traditional "who gets access" to what secret defined in regular ACLs.

Vault sends audit information to a SIEM system or logging backend via Syslog, File or Socket. Vault will not respond if it cannot provide audit information appropriately.

Ultimately Vault can either store or generate secrets dynamically. By virtue of "mounting" an engine:

Static secrets can be stored and versioned using the KV/2 engine. Secrets of different types can be dynamically generated using different engines, for Databases, SSH / AD access, PKI (X.509 Certificates) among others.

![image](https://user-images.githubusercontent.com/20936398/142029628-d5de6a09-ae25-4ba5-9306-495e9ff1a086.png)

## TODO: 

Let the Travis API environment variables for an individual repository return, It is possible to use the repository id or slug in the request. So for example:


```bash
GET /repo/{provider}/{repository.id}/env_vars
Template Variable	Type	Description
provider	Unknown	Documentation missing.
repository.id	Integer	Value uniquely identifying the repository.
```

Author(s): [Montana Mendy](https://www.github.com/montana)
