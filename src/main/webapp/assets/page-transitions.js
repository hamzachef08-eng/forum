(function () {
  function shouldHandleLink(link, event) {
    if (!link || !link.href) return false;
    if (event.defaultPrevented) return false;
    if (event.button !== 0) return false;
    if (event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) return false;
    if (link.target && link.target !== '_self') return false;
    if (link.hasAttribute('download')) return false;

    const hrefAttr = (link.getAttribute('href') || '').trim();
    if (!hrefAttr || hrefAttr.startsWith('#') || hrefAttr.startsWith('javascript:')) return false;

    let url;
    try {
      url = new URL(link.href, window.location.href);
    } catch (e) {
      return false;
    }

    if (url.origin !== window.location.origin) return false;
    if (url.href === window.location.href) return false;
    return true;
  }

  function shouldHandleForm(form, event) {
    if (!form) return false;
    if (event.defaultPrevented) return false;
    if (form.target && form.target !== '_self') return false;

    let action = form.getAttribute('action') || window.location.href;
    let url;
    try {
      url = new URL(action, window.location.href);
    } catch (e) {
      return false;
    }

    return url.origin === window.location.origin;
  }

  function startLeaveAnimation() {
    document.body.classList.remove('page-enter', 'page-enter-active');
    document.body.classList.add('page-leave');
  }

  function enableEnterAnimation() {
    document.body.classList.add('page-enter');
    window.requestAnimationFrame(function () {
      document.body.classList.add('page-enter-active');
    });
  }

  function enableLeaveAnimationForLinks() {
    document.addEventListener('click', function (event) {
      const link = event.target.closest('a');
      if (!shouldHandleLink(link, event)) return;

      event.preventDefault();
      startLeaveAnimation();

      window.setTimeout(function () {
        window.location.assign(link.href);
      }, 180);
    }, true);
  }

  function enableLeaveAnimationForForms() {
    document.addEventListener('submit', function (event) {
      const form = event.target;
      if (!shouldHandleForm(form, event)) return;
      if (form.dataset.transitioning === '1') return;

      event.preventDefault();
      form.dataset.transitioning = '1';
      startLeaveAnimation();

      window.setTimeout(function () {
        form.submit();
      }, 140);
    }, true);
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function () {
      enableEnterAnimation();
      enableLeaveAnimationForLinks();
      enableLeaveAnimationForForms();
    });
  } else {
    enableEnterAnimation();
    enableLeaveAnimationForLinks();
    enableLeaveAnimationForForms();
  }
})();
