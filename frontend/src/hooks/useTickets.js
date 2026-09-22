import { useState } from 'react';
import { api } from '../api';
import useRefresh from './useRefresh';

export default function useTickets(revision) {
  const [data, setData] = useState({ items: [], total: 0 });
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [sortField, setSortField] = useState('id');
  const [sortDirection, setSortDirection] = useState('asc');
  const [filters, setFilters] = useState({});
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  useRefresh(async (isActive) => {
    try {
      const parameters = new URLSearchParams({
        page, size: pageSize, sort: sortField, direction: sortDirection, ...filters,
      });
      const response = await api(`/tickets?${parameters}`);
      if (!isActive()) return;
      if (page > 0 && page * pageSize >= Number(response.total)) {
        setPage(Math.max(0, Math.ceil(Number(response.total) / pageSize) - 1));
      }
      setData(response);
      setError(null);
    } catch (requestError) {
      if (isActive()) setError(requestError);
    } finally {
      if (isActive()) setLoading(false);
    }
  }, [page, pageSize, sortField, sortDirection, JSON.stringify(filters), revision]);

  function applyFilter(fieldName, filterValue) {
    setFilters((previousFilters) => ({ ...previousFilters, [fieldName]: filterValue }));
    setPage(0);
  }

  function removeFilter(fieldName) {
    setFilters((previousFilters) => {
      const remainingFilters = { ...previousFilters };
      delete remainingFilters[fieldName];
      return remainingFilters;
    });
    setPage(0);
  }

  function clearFilters() {
    setFilters({});
    setPage(0);
  }

  function changePageSize(size) {
    setPageSize(size);
    setPage(0);
  }

  function changeSort(fieldName) {
    setSortField(fieldName);
    setPage(0);
  }

  function changeDirection(direction) {
    setSortDirection(direction);
    setPage(0);
  }

  return {
    data, page, pageSize, sortField, sortDirection, filters, error, loading,
    setPage, changePageSize, changeSort, changeDirection, applyFilter, removeFilter, clearFilters,
  };
}
