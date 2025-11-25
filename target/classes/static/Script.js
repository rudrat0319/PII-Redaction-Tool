const emailChk = document.getElementById("emailChk");
const nameChk = document.getElementById("nameChk");
const phoneChk = document.getElementById("phoneChk");
const addressChk = document.getElementById("addressChk");
const allChk = document.getElementById("allChk");

allChk.addEventListener("change", () => {
    const checked = allChk.checked;
    emailChk.checked = checked;
    nameChk.checked = checked;
    phoneChk.checked = checked;
    addressChk.checked = checked;
});

document.getElementById("cleanBtn").addEventListener("click", async () => {
    const text = document.getElementById("textInput").value;
    const pdfFile = document.getElementById("pdfInput").files[0];

    const options = {
        email: emailChk.checked,
        name: nameChk.checked,
        phone: phoneChk.checked,
        address: addressChk.checked
    };

    if (pdfFile) {
        await handlePdfRedaction(pdfFile, options);
    } else if (text.trim().length > 0) {
        await handleTextRedaction(text, options);
    } else {
        alert("Please enter text or upload a PDF.");
    }
});

async function handleTextRedaction(text, options) {
    const response = await fetch("/api/redact/text", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ text, ...options })
    });

    if (!response.ok) {
        alert(await response.text());
        return;
    }

    const result = await response.json();
    updateUI(result, false);
}

async function handlePdfRedaction(file, options) {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("redactEmail", options.email);
    formData.append("redactName", options.name);
    formData.append("redactPhone", options.phone);
    formData.append("redactAddress", options.address);

    const response = await fetch("/api/redact/pdf", {
        method: "POST",
        body: formData
    });

    if (!response.ok) {
        alert(await response.text());
        return;
    }

    const result = await response.json();
    updateUI(result, true);
}

function updateUI(result, isPdf) {
    const originalBox = document.getElementById("originalText");
    const redactedBox = document.getElementById("redactedText");
    const summaryBox = document.getElementById("summaryText");
    const downloadBtn = document.getElementById("downloadBtn");

    if (isPdf) {
        originalBox.textContent = "";
        redactedBox.textContent = "";
    } else {
        originalBox.textContent = result.originalText || "";
        redactedBox.textContent = result.redactedText || "";
    }

    if (result.summary) {
        const s = result.summary;
        summaryBox.textContent =
            `Emails: ${s.emailCount}, Phones: ${s.phoneCount}, Names: ${s.nameCount}, ` +
            `Addresses: ${s.addressCount}, Total removed: ${s.totalPiiFound}`;
    } else {
        summaryBox.textContent = "";
    }

    if (isPdf && result.redactedPdfUrl) {
        downloadBtn.style.display = "inline-block";
        downloadBtn.onclick = () => window.location.href = result.redactedPdfUrl;
    } else {
        downloadBtn.style.display = "none";
    }
}