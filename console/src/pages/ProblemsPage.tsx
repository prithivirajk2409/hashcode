import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Outlet } from "react-router";
import { problemListApi } from "../service/ApiService";
import "./../pages/ProblemsPage.css";

type Problem = {
  id: number;
  title: string;
  acceptance: string;
  difficulty: string;
  isSubmitted: number;
  slug: string;
};

function formatStatusToTitleCase(status: string): string {
  return status
    .toLowerCase()
    .split("_")
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join(" ");
}
const UpIcon: React.FC = () => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    width="12"
    height="12"
    viewBox="0 0 24 24"
  >
    <path d="M12 0l-8 10h16l-8-10zm3.839 16l-3.839 4.798-3.839-4.798h7.678zm4.161-2h-16l8 10 8-10z" />
  </svg>
);

const DownIcon: React.FC = () => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    width="12"
    height="12"
    viewBox="0 0 24 24"
  >
    <path d="M12 3.202l3.839 4.798h-7.678l3.839-4.798zm0-3.202l-8 10h16l-8-10zm8 14h-16l8 10 8-10z" />
  </svg>
);

const UpDownIcon: React.FC = () => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    width="12"
    height="12"
    viewBox="0 0 24 24"
  >
    <path d="M12 3.202l3.839 4.798h-7.678l3.839-4.798zm0-3.202l-8 10h16l-8-10zm3.839 16l-3.839 4.798-3.839-4.798h7.678zm4.161-2h-16l8 10 8-10z" />
  </svg>
);

const ProblemsPage: React.FC = () => {
  const [pageNumber, setPageNumber] = useState<number>(1);
  const [problemsList, setProblemsList] = useState<Problem[]>([]);
  const [totalPagesCount, setTotalPagesCount] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const fetchProblems = async () => {
      setLoading(true);
      await problemListApi(pageNumber).then((response) => {
        if (response) {
          const data = response?.data.data;
          const temp: Problem[] = data.questions.map((item: any) => ({
            id: item.problemId,
            title: item.problemName,
            acceptance: item.acceptanceRate.toFixed(1) + "%",
            difficulty: item.difficulty,
            isSubmitted: item.status,
            slug: item.slug,
          }));
          setLoading(false);
          setProblemsList(temp);
          setTotalPagesCount(data.pagesCount);
        }
      });
    };
    fetchProblems();
  }, []);

  const navigate = useNavigate();
  const handlePageChange = (page: number) => {
    setPageNumber(page);
  };

  const handleClick = (problem: Problem) => {
    navigate(`/problems/${problem.slug}`);
  };

  const [sortConfig, setSortConfig] = useState<{
    key: keyof Problem | null;
    direction: "ascending" | "descending" | null;
  }>({ key: null, direction: null });

  const requestSort = (key: keyof Problem) => {
    let direction: "ascending" | "descending" = "ascending";
    if (sortConfig.key === key && sortConfig.direction === "ascending") {
      direction = "descending";
    }
    setSortConfig({ key, direction });
  };

  const sortedProblems = [...problemsList].sort((a, b) => {
    if (sortConfig.key) {
      let aValue = a[sortConfig.key];
      let bValue = b[sortConfig.key];
      if (
        typeof aValue === "string" &&
        typeof bValue === "string" &&
        sortConfig.key === "acceptance"
      ) {
        aValue = parseFloat(aValue.replace("%", ""));
        bValue = parseFloat(bValue.replace("%", ""));
      }
      if (
        typeof aValue === "string" &&
        typeof bValue === "string" &&
        sortConfig.key === "difficulty"
      ) {
        aValue = getDifficultyOrder(aValue);
        bValue = getDifficultyOrder(bValue);
      }
      if (aValue < bValue) {
        return sortConfig.direction === "ascending" ? -1 : 1;
      }
      if (aValue > bValue) {
        return sortConfig.direction === "ascending" ? 1 : -1;
      }
    }
    return 0;
  });

  const renderSortIcon = (
    direction: "ascending" | "descending" | null,
    key: string
  ) => {
    if (direction === null || sortConfig.key !== key) {
      return <UpDownIcon />;
    }
    if (direction === "ascending") {
      return <UpIcon />;
    }
    return <DownIcon />;
  };

  const renderPaginationButtons = () => {
    const buttons = [];
    const siblingCount = 1;

    buttons.push(
      <button
        key={1}
        onClick={() => handlePageChange(1)}
        disabled={pageNumber === 1}
        className={`pagination-btn ${pageNumber === 1 ? "active" : ""}`}
      >
        1
      </button>
    );

    if (totalPagesCount === 1) {
      return buttons;
    }

    // Add ellipsis if current page is far from the first pages
    if (pageNumber > 3) {
      buttons.push(
        <span key="left-dots" className="dots">
          ...
        </span>
      );
    }

    // Add pages around the current page
    for (
      let i = Math.max(2, pageNumber - siblingCount);
      i <= Math.min(totalPagesCount - 1, pageNumber + siblingCount);
      i++
    ) {
      buttons.push(
        <button
          key={i}
          onClick={() => handlePageChange(i)}
          className={`pagination-btn ${pageNumber === i ? "active" : ""}`}
        >
          {i}
        </button>
      );
    }

    if (totalPagesCount - (pageNumber + 2) > 0) {
      buttons.push(
        <span key="right-dots" className="dots">
          ...
        </span>
      );
    }

    buttons.push(
      <button
        key={totalPagesCount}
        onClick={() => handlePageChange(totalPagesCount)}
        disabled={pageNumber === totalPagesCount}
        className={`pagination-btn ${
          pageNumber === totalPagesCount ? "active" : ""
        }`}
      >
        {totalPagesCount}
      </button>
    );

    return buttons;
  };

  return (
    <div className="min-h-screen container mx-auto p-4">
      {loading ? (
        <div className="problems-page-loader-container">
        <div className="loader-container ">
          <div className="loader">
            <svg className="circular" viewBox="25 25 50 50">
              <circle className="path" cx="50" cy="50" r="20" fill="none" stroke-width="2" stroke-miterlimit="10"/>
                </svg>
            </div>
          </div>
        </div>
      ) : (
        <div className="problem-page-master-container">
          <table className="min-w-full table-auto bg-white shadow-md rounded-md">
            <thead>
              <tr>
                <th className="px-4 py-2 text-center">
                  Status
                  <button
                    className="ml-2"
                    onClick={() => requestSort("isSubmitted")}
                  >
                    {renderSortIcon(sortConfig.direction, "isSubmitted")}
                  </button>
                </th>
                <th className="px-4 py-2 text-center">
                  Title
                  <button className="ml-2" onClick={() => requestSort("id")}>
                    {renderSortIcon(sortConfig.direction, "id")}
                  </button>
                </th>
                <th className="px-4 py-2 text-center">
                  Acceptance
                  <button
                    className="ml-2"
                    onClick={() => requestSort("acceptance")}
                  >
                    {renderSortIcon(sortConfig.direction, "acceptance")}
                  </button>
                </th>
                <th className="px-4 py-2 text-center">
                  Difficulty
                  <button
                    className="ml-2"
                    onClick={() => requestSort("difficulty")}
                  >
                    {renderSortIcon(sortConfig.direction, "difficulty")}
                  </button>
                </th>
              </tr>
            </thead>
            <tbody>
              {sortedProblems.map((problem) => (
                <tr key={problem.id} className="border-t">
                  {/* Column 1: Submission status */}
                  <td className="px-4 py-2 text-center">
                    {problem.isSubmitted === 1 ? (
                      <span className="text-green-500">✔</span>
                    ) : problem.isSubmitted === 0 ? (
                      <span className="text-red-500">✘</span>
                    ) : problem.isSubmitted === -1 ? (
                      <span>&nbsp;</span>
                    ) : null}
                  </td>

                  {/* Column 2: Title */}
                  <td
                    className="px-4 py-2 text-left cursor-pointer"
                    onClick={() => handleClick(problem)}
                  >
                    {problem.id + ". " + problem.title}
                  </td>

                  {/* Column 3: Acceptance */}
                  <td className="px-4 py-2 text-center">
                    {problem.acceptance}
                  </td>

                  {/* Column 4: Difficulty */}
                  <td
                    className={`px-4 py-2 text-center ${getDifficultyColor(
                      problem.difficulty
                    )}`}
                  >
                    {formatStatusToTitleCase(problem.difficulty)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className="pagination-container">
            <button
              onClick={() => handlePageChange(pageNumber - 1)}
              disabled={pageNumber === 1}
              className="pagination-btn"
            >
              &lt;
            </button>

            {renderPaginationButtons()}

            <button
              onClick={() => handlePageChange(pageNumber + 1)}
              disabled={pageNumber === totalPagesCount}
              className="pagination-btn"
            >
              &gt;
            </button>
          </div>
        </div>
      )}
      <Outlet />
    </div>
  );
};

const getDifficultyColor = (difficulty: string) => {
  switch (difficulty) {
    case "EASY":
      return "text-green-500";
    case "MEDIUM":
      return "text-yellow-500";
    case "HARD":
      return "text-red-500";
    default:
      return "";
  }
};

const getDifficultyOrder = (difficulty: string) => {
  switch (difficulty) {
    case "EASY":
      return 1;
    case "MEDIUM":
      return 2;
    case "HARD":
      return 3;
    default:
      return "";
  }
};

export default ProblemsPage;
