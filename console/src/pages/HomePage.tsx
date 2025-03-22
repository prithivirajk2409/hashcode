import React from "react";
import { Link } from "react-router-dom";

const HomePage: React.FC = () => {
  return (
    <div className="min-h-screen flex flex-col">
      <section className="flex-grow bg-gray-800 text-white">
        <div className="max-w-7xl mx-auto p-8 text-center">
          <h1 className="text-4xl font-bold mb-4">
            A New Way to Master Competitive Programming
          </h1>
          <p className="mb-6 text-lg">
            Hashcode is the best platform to enhance your skills, test your
            knowledge, and prepare for competitive programming.
          </p>
          <button className="bg-green-500 px-6 py-3 rounded-lg text-white text-lg hover:bg-green-600">
            <Link to="/problems">Get Started</Link>
          </button>
        </div>
      </section>

      <section className="bg-white">
        <div className="max-w-7xl mx-auto p-8">
          <h2 className="text-2xl font-bold text-center mb-6">
            Start Exploring
          </h2>
          <p className="text-center mb-8">
            Explore a well-organized platform that provides structured paths to
            help you progress towards your next programming challenge.
          </p>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div className="p-6 bg-gray-100 rounded-lg shadow-md hover:shadow-lg">
              <h3 className="text-xl font-bold mb-2">Problem Solving</h3>
              <p>
                Practice from a vast collection of problems covering various
                algorithms and data structures.
              </p>
            </div>
            <div className="p-6 bg-gray-100 rounded-lg shadow-md hover:shadow-lg">
              <h3 className="text-xl font-bold mb-2">Competitions</h3>
              <p>
                Join coding competitions and improve your skills under pressure
                with our timed challenges.
              </p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default HomePage;
