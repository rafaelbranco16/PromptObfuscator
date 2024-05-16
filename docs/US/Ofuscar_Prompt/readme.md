[Back](../README.md)

# US001

## Overshadow a *prompt*

## 1. Requirements

### 1.1. Client Acceptance Criteria
1. The format of each pii obfuscation must be of the type {typeOfPii_numberOnPrompt_reqId}
2. Each obfuscation must be within the context of the prompt.\
**Example**: My name is Rafael. \
**Response**: My name is {name_1_id} 
3. It must be accepted tests via request
4. The PII must not be recognizable on the response
### 1.2 Specifications

### 1.3 Dependencies

This functionality has no dependency with any other

## 2. Analysis

For this functionality, it is necessary to carry out an extensive analysis of the tools to be used and the possible errors
from them. To obfuscate a *prompt*, i.e. hide personal data from it, you can use for example
a NER or a trained LLM. In this case, for the time being, an LLM is being used to detect personal data, or PII. 
Not all the same personal data should be hidden, so the initial *prompt* should take into account
a list of which personal data will be omitted. Therefore, the functionality will use an LLM that will receive a
*prompt* with PII that will be omitted based on a list that is sent in the request. The request also takes
into account the temperature for the request to the LLM, i.e. how predictable the next *tokens* to be read will be.

## 3. Design

### 3.1.1 Nível 2 Process View
![Process_View_Level_2](./images/VP_N2.svg)

### 3.1.2 Nível 3 Process View
![Process_View_Level_3](./images/VP_N3.svg)
### 3.2. Nível 3 Implementation View

### 3.3. Data Structure

#### 3.3.1 Pedidos

```JSON
{
  "prompt": "Example prompt: my email is: email@email.com",
  "keywords": ["email"]
}
```

#### 3.3.2 Responses
```JSON
{
  "id": "UUID",
  "code": 200,
  "prompt": "Example prompt: my email is: {email_number_UUI}"
}
```
### 3.4. Tests

* ChatGptService Tests

| Test                            | Expected Result                      |
|:--------------------------------|:-------------------------------------|
| Empty constructor               | Do not throw and not null            |
| Constructor with chat model     | Do not throw and not null            |
| Constructor with invalid model  | Not null because of the base model   |
| Build Base Model                | Do not throw and not null            |
| Change model with valid model   | Do not throw                         |
| Change model with invalid model | Do not throw and uses the same model |

* PiiRevisionService Tests

| Test                                            | Expected Result                      |
|:------------------------------------------------|:-------------------------------------|
| Needs higher revision with valid Prompt         | True                                 |
| Does not need higher revision with valid Prompt | False                                |
| Needs higher revision with invalid Prompt       | False                                |
| Needs higher revision with no connection to LLM | Throw LLMRequestException            |

* OvershadowingService Tests

| Test                                                      | Expected Result                                                                                                                                                                                                        |
|:----------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Obfuscate with valid Prompt                               | My name is {Name_1_"+reqId+"}. Im {Age_1_"+reqId+"} years.                                                                                                                                                             |
| Obfuscate with null Prompt                                | Throw exception with the message argument \"content\" is null                                                                                                                                                          |
| Obfuscate with invalid JSON                               | Throws OvershadowingJsonParseException                                                                                                                                                                                 |
| Obfuscate with no PII in the sentence                     | Does not throw                                                                                                                                                                                                         |
| Obfuscate with no PII in the sentence                     | The prompt is the same as without obfuscation                                                                                                                                                                          |
| Obfuscate with repeated PIIs                              | My name is {Name_1_"+reqIdTemplate+"}. Im {Age_1_"+reqIdTemplate+"} years." + "I work with {Name_2_"+reqIdTemplate+"}. Me, {Name_3_"+reqIdTemplate+"} " + "have no problem in working with {Name_4_"+reqIdTemplate+"}  |
| Obfuscate With 5After With Right Chars At Wrong Positions | My name is {Name_1_"+reqIdTemplate+"}. Im {Age_1_"+reqIdTemplate+"} years. " +"I work with {Name_2_"+reqIdTemplate+"}. Me, {Name_3_"+reqIdTemplate+"} " + "have no problem in working with {Name_4_"+reqIdTemplate+"}  | 
| Obfuscate with wrong 5After                               | "My name is {Name_1_"+reqIdTemplate+"}. Im {Age_1_"+reqIdTemplate+"} years. " +"I work with {Name_2_"+reqIdTemplate+"}. Me, {Name_3_"+reqIdTemplate+"} " +"have no problem in working with {Name_4_"+reqIdTemplate+"}" |

* Pii Tests

| Test                                    | Expected Result                                                                       |
|:----------------------------------------|:--------------------------------------------------------------------------------------|
| Create valid PII                        | Not null                                                                              |
| Create PII with null ID                 | Throws InvalidPIIException with the message: "The id, " + null + ", is invalid."      |
| Create PII with empty ID                | Throws InvalidPIIException with the message: "The id, , is invalid."                  |
| Create PII with invalid ID format       | Throws InvalidPIIException with the message: "The id," + id + ", is invalid.")        |
| Create PII with null content            | Throws InvalidPIIException with the message: "The content, " + null + ", is invalid." |
| Crate PII with empty content            | Throws InvalidPIIException with the message: "The content, , is invalid."             |
| Create PII withe invalid content format | Throws InvalidPIIException: "The content,"+ content + ", is invalid."                 |

* Prompt Tests

| Test                              | Expected Result                                         |
|:----------------------------------|:--------------------------------------------------------|
| Create valid Prompt               | Not null                                                |
| Create valid Prompt               | Prompt String equals to "prompt"                        |
| Create valid Prompt               | Does not throw                                          |
| Create Prompt with invalid String | Does not throw                                          |
| Create Prompt with invalid String | Returns an empty String                                 |
| Add a valid PII to the prompt     | Add one to the list size                                |
| Add a valid PII to the prompt     | The PII is on the list                                  |
| Add a invalid PII to the prompt   | Keeps the list size                                     |
| Find PII Strings in prompt        | Has all the PIIs on the prompt inside the returned list |
| Add a list with invalid PIIs      | The invalid PIIs are not added                          |

## 4. Implementation
The classes involved on this US are:

* [ObfuscationController](../../../src/main/java/prompt/overshadowing/controllers/ObfuscationController.java)
* [ObfuscationService](../../../src/main/java/prompt/overshadowing/services/OvershadowingService.java)
* [Pii](../../../src/main/java/prompt/overshadowing/model/Pii.java)
* [Prompt](../../../src/main/java/prompt/overshadowing/model/Prompt.java)
* [ILlmService](../../../src/main/java/prompt/overshadowing/services/interfaces/ILlmModelService.java)
* [ChatGptService](../../../src/main/java/prompt/overshadowing/services/ChatGptService.java)
* [IPIIRevisionService](../../../src/main/java/prompt/overshadowing/services/interfaces/IPIIRevisionService.java)
* [PiiRevisionService](../../../src/main/java/prompt/overshadowing/services/PIIRevisionService.java)
* [PiiRepository](../../../src/main/java/prompt/overshadowing/repositories/PiiRepository.java)

## 5. Integration

## 6. Observations