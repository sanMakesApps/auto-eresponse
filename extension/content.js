
console.log("Email Writer extension- content script loaded");

function getEmailContent() {
    const selectors = ['.h7', '.a3s.aiL', '.gmail_quote', '[role="presentation"]'];


    const content = selectors
        .map(selector => document.querySelector(selector))
        .find(el => el !== null);

    return content ? content.innerText.trim() : "";


}

function findComposeToolbar() {
    const selectors = ['.btC', '.aDh', '[role="toolbar"]', '.gU.Up'];

    const toolbar = selectors
        .map(selector => document.querySelector(selector))
        .find(el => el !== null);

    return toolbar || null;

}

function createAIButton() {
    const button = document.createElement('div');
    button.className = "T-I J-J5-Ji aoO v7 T-I-atl L3";
    button.style.marginRight = '8px';
    button.style.borderRadius = '5px';
    button.innerHTML = "AI Reply";
    button.setAttribute('role', 'button');
    button.setAttribute('data-tooltip', 'Generate AI reply');
    return button;
}

function injectButton() {
    //Create Button
    const existingButton = document.querySelector('.ai-reply-button');
    if (existingButton) existingButton.remove();

    const toolbar = findComposeToolbar();
    if (!toolbar) {
        console.log("Toolbar not found");
        return;
    }
    console.log("Toolbar found, creating the AI button");

    const button = createAIButton();
    button.classList.add('ai-reply-button');

    button.addEventListener("click", async () => {
        try {
            button.innerHTML = 'Generating...';
            button.disabled = true;

            //onClick: Query backend API and send content, and tone
            const emailContent = getEmailContent();
            const response = await fetch('http://localhost:8080/api/email/generate', {
                method: 'POST',
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    emailContent: emailContent,
                    tone: "professional"
                })

            });

            if (!response.ok) {
                throw new Error("API request failed");
            }

            const generatedReply = await response.text();
            const composeBox = document.querySelector('[role="textbox"][g_editable="true"]');

            if (composeBox) {
                composeBox.focus();
                document.execCommand('insertText', false, generatedReply);
            } else {
                console.error("Compose box not found.");
            }

        } catch (error) {
            console.error(error);

            alert("Reply generation failed.");

        } finally {
            button.innerHTML = "AI Reply";
            button.disabled = false;
        }
    });

    //Inject Button to gmail UI
    toolbar.insertBefore(button, toolbar.firstChild);


    //Generated reply loaded back in gmail UI
}
const observer = new MutationObserver((mutations) => {


    mutations.forEach((mutation) => {
        const addedNodes = Array.from(mutation.addedNodes);
        const hasComposeElements = addedNodes.some(node =>
            //checking if the node is html element and not texts or comments. And some to check at least one node satisfies condition.
            (node.nodeType === Node.ELEMENT_NODE) && (node.matches('.aDh, .btC, [role="dialog"]') || node.querySelector('.aDh, .btC, [role="dialog"]'))
        );
        if (hasComposeElements) {
            console.log("Compose Window Detected");
            //Inject button called with .5s buffer to give gmail time to load before any action. We inject if we find any element that indicate the compose button is added to the UI.
            setTimeout(injectButton, 500);

        }

    })
});

observer.observe(document.body, {
    childList: true,
    subtree: true
});