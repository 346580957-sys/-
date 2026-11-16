/* =========================================================
   华曼棠慢病智库 — interactions
   ========================================================= */

(function () {
  "use strict";

  const $ = (sel, scope) => (scope || document).querySelector(sel);
  const $$ = (sel, scope) => Array.from((scope || document).querySelectorAll(sel));
  const reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

  /* ---------- header state ---------- */
  const header = $("#site-header");
  if (header) {
    const updateHeader = () => {
      header.classList.toggle("scrolled", window.scrollY > 40);
    };
    window.addEventListener("scroll", updateHeader, { passive: true });
    updateHeader();
  }

  /* ---------- reveal on scroll ---------- */
  const revealEls = $$("[data-reveal]");
  if ("IntersectionObserver" in window && !reduceMotion) {
    const revealObserver = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (!entry.isIntersecting) return;
          entry.target.classList.add("is-visible");
          revealObserver.unobserve(entry.target);
        });
      },
      { threshold: 0.12, rootMargin: "0px 0px -6% 0px" }
    );
    revealEls.forEach((el) => revealObserver.observe(el));
  } else {
    revealEls.forEach((el) => el.classList.add("is-visible"));
  }

  /* ---------- counters ---------- */
  const counters = $$("[data-count]");
  const animateCount = (el) => {
    const target = parseFloat(el.dataset.count);
    const decimals = parseInt(el.dataset.decimals || "0", 10);
    const duration = 1700;
    const start = performance.now();

    const tick = (now) => {
      const progress = Math.min((now - start) / duration, 1);
      const eased = 1 - Math.pow(1 - progress, 3);
      el.textContent = (target * eased).toFixed(decimals);
      if (progress < 1) {
        requestAnimationFrame(tick);
      } else {
        el.textContent = target.toFixed(decimals);
      }
    };
    requestAnimationFrame(tick);
  };

  if (counters.length) {
    if ("IntersectionObserver" in window && !reduceMotion) {
      const countObserver = new IntersectionObserver(
        (entries) => {
          entries.forEach((entry) => {
            if (!entry.isIntersecting) return;
            animateCount(entry.target);
            countObserver.unobserve(entry.target);
          });
        },
        { threshold: 0.6 }
      );
      counters.forEach((el) => countObserver.observe(el));
    } else {
      counters.forEach((el) => {
        el.textContent = parseFloat(el.dataset.count).toFixed(
          parseInt(el.dataset.decimals || "0", 10)
        );
      });
    }
  }

  /* ---------- narrative steps ---------- */
  const steps = $$(".n-step");
  const frames = $$(".nv-frames img");
  const nvNum = $(".nv-num");
  const nvLabel = $(".nv-label");
  const labels = ["读懂指标", "排好一日", "看见变化"];

  const setActiveStep = (index) => {
    steps.forEach((step, i) => step.classList.toggle("is-active", i === index));
    frames.forEach((frame, i) => frame.classList.toggle("is-active", i === index));
    if (nvNum) nvNum.textContent = "0" + (index + 1);
    if (nvLabel) nvLabel.textContent = labels[index] || "";
  };

  if (steps.length && "IntersectionObserver" in window) {
    const stepObserver = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (!entry.isIntersecting) return;
          setActiveStep(Number(entry.target.dataset.step));
        });
      },
      { rootMargin: "-46% 0px -46% 0px", threshold: 0 }
    );
    steps.forEach((step) => stepObserver.observe(step));
  }

  /* ---------- floating image preview ---------- */
  const preview = $(".float-preview");
  const previewImg = preview ? $("img", preview) : null;
  const canHover = window.matchMedia("(hover: hover) and (pointer: fine)").matches;

  if (preview && previewImg && canHover) {
    $$("[data-preview]").forEach((row) => {
      const img = new Image();
      img.src = row.dataset.preview;
    });

    let targetX = 0;
    let targetY = 0;
    let currentX = 0;
    let currentY = 0;
    let rafId = null;

    const render = () => {
      currentX += (targetX - currentX) * 0.16;
      currentY += (targetY - currentY) * 0.16;
      preview.style.transform =
        "translate3d(" + currentX.toFixed(1) + "px," + currentY.toFixed(1) + "px,0)";
      rafId = requestAnimationFrame(render);
    };

    const position = (event) => {
      const w = preview.offsetWidth || 280;
      const h = preview.offsetHeight || 210;
      const x = Math.min(event.clientX + 26, window.innerWidth - w - 16);
      const y = Math.min(
        Math.max(event.clientY - h / 2, 16),
        window.innerHeight - h - 16
      );
      targetX = Math.max(16, x);
      targetY = y;
    };

    $$("[data-preview]").forEach((row) => {
      row.addEventListener("pointerenter", (event) => {
        previewImg.src = row.dataset.preview;
        preview.classList.add("is-visible");
        position(event);
        currentX = targetX;
        currentY = targetY;
        if (!rafId) rafId = requestAnimationFrame(render);
      });
      row.addEventListener("pointermove", position);
      row.addEventListener("pointerleave", () => {
        preview.classList.remove("is-visible");
        if (rafId) {
          cancelAnimationFrame(rafId);
          rafId = null;
        }
      });
    });
  }

  /* ---------- mobile drawer ---------- */
  const menuBtn = $("#menu-btn");
  const drawer = $("#menu-drawer");
  const drawerClose = $("#drawer-close");

  const openDrawer = () => {
    if (!drawer) return;
    drawer.classList.add("is-open");
    drawer.setAttribute("aria-hidden", "false");
    menuBtn.setAttribute("aria-expanded", "true");
    document.body.style.overflow = "hidden";
  };

  const closeDrawer = () => {
    if (!drawer) return;
    drawer.classList.remove("is-open");
    drawer.setAttribute("aria-hidden", "true");
    menuBtn.setAttribute("aria-expanded", "false");
    document.body.style.overflow = "";
  };

  if (menuBtn) menuBtn.addEventListener("click", openDrawer);
  if (drawerClose) drawerClose.addEventListener("click", closeDrawer);
  if (drawer) {
    $$("a", drawer).forEach((link) => link.addEventListener("click", closeDrawer));
  }
  document.addEventListener("keydown", (event) => {
    if (event.key === "Escape") closeDrawer();
  });

  /* ---------- booking dialog ---------- */
  const dialog = $("#booking");
  const bookingForm = $("#booking-form");
  const bookingError = $("#booking-error");
  const bookingSuccess = $("#booking-success");
  const bookingBody = $(".booking-body");

  const openBooking = () => {
    if (!dialog) return;
    closeDrawer();
    if (bookingBody) bookingBody.classList.remove("is-success");
    if (bookingForm) bookingForm.hidden = false;
    if (bookingSuccess) bookingSuccess.hidden = true;
    if (bookingError) bookingError.textContent = "";
    if (typeof dialog.showModal === "function") {
      dialog.showModal();
    } else {
      dialog.setAttribute("open", "");
    }
  };

  const closeBooking = () => {
    if (!dialog) return;
    if (typeof dialog.close === "function") {
      dialog.close();
    } else {
      dialog.removeAttribute("open");
    }
  };

  $$("[data-booking]").forEach((btn) => btn.addEventListener("click", openBooking));

  const bookingClose = $("#booking-close");
  if (bookingClose) bookingClose.addEventListener("click", closeBooking);
  if (dialog) {
    dialog.addEventListener("click", (event) => {
      if (event.target === dialog) closeBooking();
    });
  }

  if (bookingForm) {
    bookingForm.addEventListener("submit", (event) => {
      event.preventDefault();
      const name = $("#name").value.trim();
      const phone = $("#phone").value.trim();

      let message = "";
      if (!name) {
        message = "请留下你的称呼，方便我们回电。";
      } else if (!/^1\d{10}$/.test(phone)) {
        message = "请填写 11 位手机号码，例如 13800000000。";
      }

      if (message) {
        if (bookingError) bookingError.textContent = message;
        return;
      }

      if (bookingError) bookingError.textContent = "";
      bookingForm.hidden = true;
      if (bookingBody) bookingBody.classList.add("is-success");
      if (bookingSuccess) bookingSuccess.hidden = false;
      bookingForm.reset();
    });
  }

  const bookingDone = $("#booking-done");
  if (bookingDone) bookingDone.addEventListener("click", closeBooking);

  if (dialog) {
    dialog.addEventListener("close", () => {
      window.setTimeout(() => {
        if (!bookingForm || !bookingSuccess) return;
        bookingForm.hidden = false;
        bookingSuccess.hidden = true;
        if (bookingBody) bookingBody.classList.remove("is-success");
        if (bookingError) bookingError.textContent = "";
      }, 200);
    });
  }
})();
