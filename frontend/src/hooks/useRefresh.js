import { useEffect, useRef } from 'react';

export default function useRefresh(load, dependencies = []) {
  const latest = useRef(load);
  latest.current = load;
  useEffect(() => {
    let active = true;
    let loading = false;

    async function refresh() {
      if (loading) return;
      loading = true;
      try {
        await latest.current(() => active);
      } finally {
        loading = false;
      }
    }

    refresh();
    const timer = setInterval(refresh, 5000);
    return () => {
      active = false;
      clearInterval(timer);
    };
  }, dependencies);
}
